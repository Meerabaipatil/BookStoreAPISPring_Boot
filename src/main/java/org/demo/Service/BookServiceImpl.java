package org.demo.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.demo.DTO.BookDTO;
import org.demo.Dao.BookDao;
import org.demo.Entity.Book;
import org.demo.Entity.User;
import org.demo.ExceptionHandling.BookIdNotFoundException;
import org.demo.ModelMapper.ModelMapper;
import org.demo.Repository.UserRepository;
import org.demo.responseStructure.ResponseStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BookServiceImpl implements BookService {

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private BookDao dao;

    @Autowired
    private UserRepository userRepo;
    
    @Autowired
    private RestTemplate restTemplate;


    private static final Logger log = LoggerFactory.getLogger(BookServiceImpl.class);

    // ================= SAVE =================
    @Override
    public ResponseEntity<ResponseStructure<BookDTO>> save(BookDTO dto) {

        Book book;

        if (dto.getUserId() != null) {
            Optional<User> userOpt = userRepo.findById(dto.getUserId());
            if (userOpt.isEmpty()) {
                return buildError("User not found with id: " + dto.getUserId(), HttpStatus.NOT_FOUND);
            }
            book = mapper.convertIntoEntity(dto, userOpt.get());
        } else {
            book = mapper.convertIntoEntity(dto, null);
        }

        Book saved = dao.save(book);
        BookDTO responseDto = mapper.convertIntoDTO(saved);

        return buildSuccess("Book saved successfully", responseDto);
    }

    // ================= FIND BY ID =================
    @Override
    public ResponseEntity<ResponseStructure<BookDTO>> findById(int id) {
        Optional<Book> opt = dao.findById(id);

        if (opt.isEmpty()) {
            throw new BookIdNotFoundException("Book not found with id: " + id);
        }

        BookDTO dto = mapper.convertIntoDTO(opt.get());
        return buildSuccess("Book found", dto);
    }

    // ================= DELETE =================
    @Override
    public ResponseEntity<ResponseStructure<BookDTO>> deleteById(int id) {
        Optional<Book> opt = dao.findById(id);

        if (opt.isEmpty()) {
            throw new BookIdNotFoundException("Book not found with id: " + id);
        }

        Book book = opt.get();
        dao.deleteById(id);

        BookDTO dto = mapper.convertIntoDTO(book);
        return buildSuccess("Book deleted", dto);
    }

    // ================= UPDATE =================
    @Override
    public ResponseEntity<ResponseStructure<BookDTO>> update(BookDTO dto, int id) {
        Optional<Book> opt = dao.findById(id);

        if (opt.isEmpty()) {
            throw new BookIdNotFoundException("Book not found with id: " + id);
        }

        Book book = opt.get();
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setPrice(dto.getPrice());
        book.setPublishesAt(dto.getPublishesAt());

        if (dto.getUserId() != null) {
            Optional<User> userOpt = userRepo.findById(dto.getUserId());
            if (userOpt.isEmpty()) {
                return buildError("User not found with id: " + dto.getUserId(), HttpStatus.NOT_FOUND);
            }
            book.setUser(userOpt.get());
        }

        Book updated = dao.update(book);
        BookDTO responseDto = mapper.convertIntoDTO(updated);

        return buildSuccess("Book updated successfully", responseDto);
    }

    // ================= FIND ALL =================
    @Override
    public ResponseEntity<ResponseStructure<List<BookDTO>>> findAll(Pageable pageable, Integer id, String title,
            String author, Integer price, LocalDate publishesAt) {

        List<Book> books = dao.findAll(pageable, id, title, author, price, publishesAt).getContent();

        List<BookDTO> list = books.stream()
                .map(b -> mapper.convertIntoDTO(b))
                .toList();

        ResponseStructure<List<BookDTO>> rs = new ResponseStructure<>();
        rs.setStatusCode(HttpStatus.OK.value());
        rs.setMessage("All books fetched");
        rs.setData(list);

        return ResponseEntity.ok(rs);
    }

    // ================= BORROW BOOK =================
    @Override
    public ResponseEntity<ResponseStructure<BookDTO>> borrowBook(int bookId, Long userId) {
        Optional<Book> bookOpt = dao.findById(bookId);
        Optional<User> userOpt = userRepo.findById(userId);

        if (bookOpt.isEmpty())
            throw new BookIdNotFoundException("Book not found with id: " + bookId);

        if (userOpt.isEmpty())
            return buildError("User not found with id: " + userId, HttpStatus.NOT_FOUND);

        Book book = bookOpt.get();

        if (book.getUser() != null) {
            return buildError("Book already borrowed", HttpStatus.BAD_REQUEST);
        }

        book.setUser(userOpt.get());
        Book updated = dao.update(book);

        return buildSuccess("Book borrowed successfully", mapper.convertIntoDTO(updated));
    }

    // ================= REMOVE USER FROM BOOK =================
    @Override
    public ResponseEntity<ResponseStructure<BookDTO>> removeUserFromBook(int bookId) {
        Optional<Book> bookOpt = dao.findById(bookId);

        if (bookOpt.isEmpty())
            throw new BookIdNotFoundException("Book not found with id: " + bookId);

        Book book = bookOpt.get();
        book.setUser(null);

        Book updated = dao.update(book);
        return buildSuccess("User removed from book", mapper.convertIntoDTO(updated));
    }

   
    @Override
    public ResponseEntity<ResponseStructure<List<BookDTO>>> getAvailableBooks() {
        List<Book> books = dao.findAvailableBooks();
        return buildList("Available books fetched", books);
    }

    @Override
    public ResponseEntity<ResponseStructure<List<BookDTO>>> getBorrowedBooks() {
        List<Book> books = dao.findBorrowedBooks();
        return buildList("Borrowed books fetched", books);
    }

    @Override
    public ResponseEntity<ResponseStructure<List<BookDTO>>> getBooksByUser(Long userId) {
        List<Book> books = dao.findBooksByUser(userId);
        return buildList("Books fetched for user " + userId, books);
    }

    @Override
    public ResponseEntity<ResponseStructure<List<BookDTO>>> getBooksByPriceRange(int min, int max) {
        List<Book> books = dao.findByPriceRange(min, max);
        return buildList("Books fetched in price range", books);
    }

    @Override
    public ResponseEntity<ResponseStructure<List<BookDTO>>> searchBooksByTitle(String title) {
        List<Book> books = dao.searchByTitle(title);
        return buildList("Books fetched by title", books);
    }

   
    private ResponseEntity<ResponseStructure<BookDTO>> buildSuccess(String msg, BookDTO dto) {
        ResponseStructure<BookDTO> rs = new ResponseStructure<>();
        rs.setStatusCode(HttpStatus.OK.value());
        rs.setMessage(msg);
        rs.setData(dto);
        return ResponseEntity.ok(rs);
    }

    private ResponseEntity<ResponseStructure<BookDTO>> buildError(String msg, HttpStatus status) {
        ResponseStructure<BookDTO> rs = new ResponseStructure<>();
        rs.setStatusCode(status.value());
        rs.setMessage(msg);
        rs.setData(null);
        return ResponseEntity.status(status).body(rs);
    }

    private ResponseEntity<ResponseStructure<List<BookDTO>>> buildList(String msg, List<Book> books) {
        List<BookDTO> list = books.stream()
                .map(b -> mapper.convertIntoDTO(b))
                .toList();

        ResponseStructure<List<BookDTO>> rs = new ResponseStructure<>();
        rs.setStatusCode(HttpStatus.OK.value());
        rs.setMessage(msg);
        rs.setData(list);

        return ResponseEntity.ok(rs);
    }
    
    
    //--------------calling dummy api -----------//
    public String callExternalApi() {

        String url = "https://jsonplaceholder.typicode.com/posts/1";

        ResponseEntity<String> response =
                restTemplate.getForEntity(url, String.class);

        return response.getBody();
    }

}
