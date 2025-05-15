package com.book.store.app.repository;

import com.book.store.app.entity.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class BookRepositoryImpl implements BookRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Book save(Book book) {
        em.persist(book);
        return book;
    }

    @Override
    public List<Book> findAll() {
        return em.createQuery("FROM Book", Book.class).getResultList();
    }
}
