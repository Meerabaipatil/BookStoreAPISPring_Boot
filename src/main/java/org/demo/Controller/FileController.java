package org.demo.Controller;

import java.util.List;

import org.demo.Entity.BookFile;
import org.demo.Service.FileServiceImpl;
import org.demo.responseStructure.ResponseStructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import lombok.Data;

@RestController
@RequestMapping("/books")
@Data
public class FileController {
	
	@Autowired
	private FileServiceImpl impl;
	
	
	@PostMapping("/save-csv")
	
    public ResponseEntity<ResponseStructure<BookFile>> save(){
		System.out.println("save() API CALLED");
		return impl.save();
	}
	
	@GetMapping("/all")
	public ResponseEntity<ResponseStructure<List<BookFile>>> findAll(){
		return impl.findAll();
	}
	
	@GetMapping("/export")
	public ResponseEntity<ResponseStructure<String>> exportToCsv() {
	    return impl.export();
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<ResponseStructure<String>> delete(@PathVariable int id){
		return impl.delete(id);
	}

	

}
