package org.demo.Repository;

import org.demo.Entity.Book;
import org.demo.Entity.BookFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<BookFile, Integer>{

}
