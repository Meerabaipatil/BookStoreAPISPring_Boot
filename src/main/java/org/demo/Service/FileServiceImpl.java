package org.demo.Service;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.demo.Dao.FileDao;
import org.demo.Entity.BookFile;
import org.demo.responseStructure.ResponseStructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.Data;

@Service
@Data
public class FileServiceImpl implements FileService {

	@Autowired
	private FileDao dao;

	// -------------save data from csv to db------------//
	@Override
	public ResponseEntity<ResponseStructure<BookFile>> save() {

		ResponseStructure<BookFile> structure = new ResponseStructure<>();

		try {
			// getClass()->return current class object ,
			// getClassLoader()-> Used to load files from the resources folder
			// getResourceAsStream()-> Search the given file and open it as inputstream(Java
			// can read file as streams)
			InputStream stream = getClass().getClassLoader().getResourceAsStream("books.csv");

			if (stream == null) {
				structure.setStatusCode(HttpStatus.NOT_FOUND.value());
				structure.setMessage("CSV file not found");
				structure.setData(null);

				return new ResponseEntity<ResponseStructure<BookFile>>(structure, HttpStatus.NOT_FOUND);

			} else {

				// inputStream(bytes)->reader (character)
				// take file stream,convert into text and prepare it to read line by line
				BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

				String line;
				// In csv file first line is header not data like name,price
				boolean firstLine = true;

				while ((line = reader.readLine()) != null) {

					// check first line ,after skipping make next line as data
					if (firstLine) {
						firstLine = false;
						continue;
					}
					String[] data = line.split(",");
					BookFile book = new BookFile();
					book.setTitle(data[0]);
					book.setAuthor(data[1]);
					book.setPrice(Integer.parseInt(data[2]));
					book.setPublishesAt(LocalDate.parse(data[3]));
					book.setCreatedAt(LocalDateTime.parse(data[4]));
					book.setUpdatedAt(LocalDateTime.parse(data[5]));

					dao.save(book);
				}
				reader.close();
				structure.setStatusCode(201);
				structure.setMessage("Products saved from CSV");
				structure.setData(null);
				return new ResponseEntity<>(structure, HttpStatus.CREATED);
			}
		} catch (Exception e) {
			e.printStackTrace();

			structure.setStatusCode(500);
			structure.setMessage("Error while reading CSV");
			structure.setData(null);

			return new ResponseEntity<>(structure, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	//-----------fetch all records from db--------------//
	@Override
	public ResponseEntity<ResponseStructure<List<BookFile>>> findAll() {

		ResponseStructure<List<BookFile>> structure = new ResponseStructure<>();
		List<BookFile> list = dao.findAll();

		structure.setStatusCode(HttpStatus.OK.value());
		structure.setMessage("All books fetched");
		structure.setData(list);

		return new ResponseEntity<>(structure, HttpStatus.OK);
	}

	// ----------------export data from db to csv------------//
	@Override
	public ResponseEntity<ResponseStructure<String>> export() {
		ResponseStructure<String> structure = new ResponseStructure<>();
		try {
			List<BookFile> all = dao.findAll();

			// Java class to write text files.
			FileWriter writer = new FileWriter("books_export.csv");
			writer.append("title,author,price,publishesAt,createdAt,updatedAt\n");

			for (BookFile file : all) {
				writer.append(file.getTitle()).append(",").append(file.getAuthor()).append(",")
						.append(String.valueOf(file.getPrice())).append(",").append(file.getPublishesAt().toString())
						.append(",").append(file.getCreatedAt().toString()).append(",")
						.append(file.getUpdatedAt().toString()).append("\n");
			}

			writer.flush();
			writer.close();

			structure.setStatusCode(HttpStatus.OK.value());
			structure.setMessage("CSV exported successfully");
			structure.setData("books_export.csv");

			return new ResponseEntity<>(structure, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();

			structure.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			structure.setMessage("Error while exporting CSV");
			structure.setData(null);

			return new ResponseEntity<>(structure, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	
	//------------delete data from db and update csv-----------//
	@Override
	public ResponseEntity<ResponseStructure<String>> delete(int id) {
		ResponseStructure<String> structure = new ResponseStructure<>();
		try {
			Optional<BookFile> byId = dao.findById(id);

			if (byId.isPresent()) {

				dao.deleteById(id);
				export();

				structure.setStatusCode(HttpStatus.OK.value());
				structure.setMessage("Book deleted successfully and CSV updated");
				structure.setData("Deleted Book ID: " + id);
				return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.OK);
			} else {
				structure.setStatusCode(HttpStatus.NOT_FOUND.value());
				structure.setMessage("Book not found with ID: " + id);
				structure.setData(null);
				return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
			}

		} catch (Exception e) {
			e.printStackTrace();

			structure.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			structure.setMessage("Error while deleting book");
			structure.setData(null);

			return new ResponseEntity<>(structure, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
