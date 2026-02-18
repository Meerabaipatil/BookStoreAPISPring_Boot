package org.demo.Repository;

import java.util.List;

import org.demo.Entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer>, JpaSpecificationExecutor<Book> {

	Page<Book> findByTitle(String title, Pageable pageable);
	
	 @Query("select b from Book b where b.user is null")
	    List<Book> findAvailableBooks();

	    
	    @Query("select b from Book b where b.user is not null")
	    List<Book> findBorrowedBooks();

	  
	    @Query("select b from Book b where b.user.id = :uid")
	    List<Book> findBooksByUser(@Param("uid") Long uid);

	 
	    @Query("select b from Book b where b.price between :min and :max")
	    List<Book> findByPriceRange(@Param("min") int min,
	                                @Param("max") int max);

	   
	    @Query("select b from Book b where lower(b.title) like lower(concat('%', :title, '%'))")
	    List<Book> searchByTitle(@Param("title") String title);
}
