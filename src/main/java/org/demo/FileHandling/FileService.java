package org.demo.FileHandling;

import java.util.List;

import org.demo.responseStructure.ResponseStructure;
import org.springframework.http.ResponseEntity;


public interface FileService {

	ResponseEntity<ResponseStructure<BookFile>> save();
	
	ResponseEntity<ResponseStructure<List<BookFile>>> findAll();
	 
	ResponseEntity<ResponseStructure<String>> export();
	
	ResponseEntity<ResponseStructure<String>> delete( int id);
	
}
