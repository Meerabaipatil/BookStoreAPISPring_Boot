package org.demo.Dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.demo.Entity.Book;
import org.demo.FileHandling.BookSpecification;
import org.demo.Repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class BookDao {

    @Autowired
    private BookRepository repository;

    public Book save(Book book) {
        return repository.save(book);
    }

    public Optional<Book> findById(int id) {
        return repository.findById(id);
    }

    public void deleteById(int id) {
        repository.deleteById(id);
    }

    public Book update(Book book) {
        return repository.save(book);
    }

    public Page<Book> findAll(Pageable pageable, Integer id, String title, String author, Integer price, LocalDate publishesAt) {
        Specification<Book> specification = BookSpecification.getSpecification(id, title, author, price, publishesAt);
        return repository.findAll(specification, pageable);
    }
    
    public List<Book> findAvailableBooks() {
        return repository.findAvailableBooks();
    }

    public List<Book> findBorrowedBooks() {
        return repository.findBorrowedBooks();
    }

    public List<Book> findBooksByUser(Long userId) {
        return repository.findBooksByUser(userId);
    }

    public List<Book> findByPriceRange(int min, int max) {
        return repository.findByPriceRange(min, max);
    }

    public List<Book> searchByTitle(String title) {
        return repository.searchByTitle(title);
    }
}
