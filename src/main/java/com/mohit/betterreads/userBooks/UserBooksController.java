package com.mohit.betterreads.userBooks;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.ModelAndView;

import com.mohit.betterreads.book.AuthorBySearch;
import com.mohit.betterreads.book.Book;
import com.mohit.betterreads.book.BookBySearch;
import com.mohit.betterreads.book.BookRepository;
import com.mohit.betterreads.user.BooksByUser;
import com.mohit.betterreads.user.BooksByUserRepository;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.client.WebClient;


@Controller
public class UserBooksController {
    private final String WORKS_ROOT_URL = "https://openlibrary.org/works/";

    private final String AUTHORS_ROOT_URL = "https://openlibrary.org/authors/";
    
    private WebClient webClient;
    private WebClient authorWebClient;
    @Autowired
    private UserBooksRepository userBooksRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BooksByUserRepository booksByUserRepository;

    public UserBooksController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(WORKS_ROOT_URL).build();

        this.authorWebClient = webClientBuilder.baseUrl(AUTHORS_ROOT_URL).build();
    }

    @PostMapping("/addUserBook")
    private ModelAndView addUserBooks(@RequestBody MultiValueMap<String, String> formData, @AuthenticationPrincipal OAuth2User principal) {
        //System.out.println("form Data" + formData);
        String userId = principal.getAttribute("login");
        String bookId = formData.getFirst("bookId");

        Book book = new Book();
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        UserBookPrimaryKey userBookPrimaryKey = new UserBookPrimaryKey();
        userBookPrimaryKey.setBookId(bookId);
        userBookPrimaryKey.setUserId(userId);
        
        UserBooks userBooks = new UserBooks();
        userBooks.setUserBookPrimaryKey(userBookPrimaryKey);
        userBooks.setRating(Integer.parseInt(formData.getFirst("rating")));
        userBooks.setStartedDate(LocalDate.parse(formData.getFirst("startedDate")));
        userBooks.setCompletedDate(LocalDate.parse(formData.getFirst("completedDate")));
        userBooks.setReadingStatus(formData.getFirst("readingStatus"));

        
        if(optionalBook.isPresent()) {

            book = optionalBook.get();

        } else {
            Mono<BookBySearch> mono = webClient.get().uri(bookId + ".json").retrieve()
                    .bodyToMono(BookBySearch.class);
                
                   
                BookBySearch bookBySearch = mono.block();
                List<String> authorNames = bookBySearch.getAuthors().stream().map(author -> {
                        String authorId = author.getAuthor().getKey().replace("/authors/", "");
                        Mono<AuthorBySearch> authorMono = authorWebClient.get().uri(authorId + ".json").retrieve().bodyToMono(AuthorBySearch.class);
                        AuthorBySearch authorBySearch = authorMono.block();

                        return authorBySearch.getPersonal_name();
                    }).collect(Collectors.toList());
                
            book.setAuthorNames(authorNames);
            book.setName(bookBySearch.getTitle());
            book.setCoverId(bookBySearch.getCovers());
            
        }

        userBooksRepository.save(userBooks);
        BooksByUser booksByUser = new BooksByUser();
        booksByUser.setId(userId);
        booksByUser.setBookId(bookId);
        booksByUser.setBookName(book.getName());
        booksByUser.setCoverIds(book.getCoverId());
        booksByUser.setAuthorNames(book.getAuthorNames());
        booksByUser.setReadingStatus(formData.getFirst("readingStatus"));
        booksByUser.setRating(Integer.parseInt(formData.getFirst("rating")));
        
        
        booksByUserRepository.save(booksByUser);
        return new ModelAndView("redirect:/books/" + bookId);
    }
}
