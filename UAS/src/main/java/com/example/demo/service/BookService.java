package com.example.demo.service;

import com.example.demo.dto.BookDto;
import com.example.demo.dto.CategoryDto;
import com.example.demo.entity.Book;
import com.example.demo.entity.Category;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public Page<BookDto> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(book -> {
                    BookDto bookDto = new BookDto();
                    bookDto.setId(book.getId());
                    bookDto.setTitle(book.getTitle());
                    bookDto.setAuthor(book.getTitle());

                    Category category = book.getCategory();
                    CategoryDto categoryDto = new CategoryDto();
                    categoryDto.setId(category.getId());
                    categoryDto.setName(category.getName());
                    bookDto.setCategory(categoryDto);

                    return bookDto;
                });
    }

    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        BookDto bookDto = new BookDto();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());

        Category category = book.getCategory();
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        bookDto.setCategory(categoryDto);

        return bookDto;

    }

    public BookDto updateBook(BookDto request) {
        Book book = bookRepository.findById(request.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        book.setAuthor(request.getAuthor());
        book.setTitle(request.getTitle());
        book.setCategory(categoryRepository.findById(request.getCategory().getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));

        bookRepository.save(book);

        return request;
    }

    public BookDto createBook(BookDto request) {
        Book book = new Book();
        book.setAuthor(request.getAuthor());
        book.setTitle(request.getTitle());
        book.setCategory(categoryRepository.findById(request.getCategory().getId()).orElseGet(() -> {
            Category category = new Category();
            category.setName(request.getCategory().getName());
            category = categoryRepository.save(category);

            request.getCategory().setId(category.getId());

            return category;
        }));

        book = bookRepository.save(book);
        request.setId(book.getId());

        return request;
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}