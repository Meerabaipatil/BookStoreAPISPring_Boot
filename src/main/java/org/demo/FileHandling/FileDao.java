package org.demo.FileHandling;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class FileDao {
	
	@Autowired
	private FileRepository repository;
	
	public BookFile save(BookFile book) {
		return repository.save(book);
	}
	
	public List<BookFile> findAll(){
		return repository.findAll();
	}
	
	public Optional<BookFile> findById(int id){
		return repository.findById(id);
		
		
	}
	
	public void deleteById(int id) {
		 repository.deleteById(id);
	}

}
