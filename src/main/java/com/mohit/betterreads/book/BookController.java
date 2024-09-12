package com.mohit.betterreads.book;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.WebClient;

import com.mohit.betterreads.userBooks.UserBookPrimaryKey;
import com.mohit.betterreads.userBooks.UserBooks;
import com.mohit.betterreads.userBooks.UserBooksRepository;

import reactor.core.publisher.Mono;


@Controller
public class BookController {
    
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserBooksRepository userBooksRepository;


    private WebClient webClient;
    private WebClient authorWebClient;
    private final String WORKS_ROOT_URL = "https://openlibrary.org/works/";

    private final String AUTHORS_ROOT_URL = "https://openlibrary.org/authors/";
    
    private final String ROOT_URL = "http://covers.openlibrary.org/b/id/";

    public BookController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(WORKS_ROOT_URL).build();

        this.authorWebClient = webClientBuilder.baseUrl(AUTHORS_ROOT_URL).build();
    }
    @GetMapping("/books/{bookId}")
    public String getBook(@PathVariable String bookId, Model model, @AuthenticationPrincipal OAuth2User principal) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        //System.out.println(optionalBook);
        String coverImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ac/No_image_available.svg/600px-No_image_available.svg.png";
        UserBooks userBooks = new UserBooks();
        
        
        if(optionalBook.isPresent()) {
            

            Book book = optionalBook.get();
            model.addAttribute("book", book);
            if(book.getCoverId() != null && book.getCoverId().size() > 0) {

                coverImageUrl = ROOT_URL + book.getCoverId().get(0) + "-L.jpg";
                model.addAttribute("coverImageURL", coverImageUrl);
            }
            
            if(principal != null && principal.getAttribute("login") != null) {
                String userId = principal.getAttribute("login");
                UserBookPrimaryKey userBookPrimaryKey = new UserBookPrimaryKey();
                userBookPrimaryKey.setBookId(bookId);
                userBookPrimaryKey.setUserId(userId);
                Optional<UserBooks> optionalUserBooks = userBooksRepository.findById(userBookPrimaryKey);
                if(optionalUserBooks.isPresent()) {
                    userBooks = optionalUserBooks.get();
                } else {
                    userBooks = new UserBooks();
                }

                System.out.println(userBooks);
                model.addAttribute("userBooks", userBooks);
                model.addAttribute("loginId", principal.getAttribute("login"));
                model.addAttribute("bookId", bookId);
            } 

            return "book";
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
                model.addAttribute("authorNames", authorNames);
                model.addAttribute("bookBySearch", bookBySearch);
                 System.out.println(bookBySearch);

                if(bookBySearch.getCovers() != null && bookBySearch.getCovers().size() > 0) {

                    coverImageUrl = ROOT_URL + bookBySearch.getCovers().get(0) + "-L.jpg";
                    model.addAttribute("coverImageURL", coverImageUrl);
                    model.addAttribute("description", bookBySearch.getDescription().getValue());
                    
                    if(principal != null && principal.getAttribute("login") != null) {
                        String userId = principal.getAttribute("login");
                        UserBookPrimaryKey userBookPrimaryKey = new UserBookPrimaryKey();
                        userBookPrimaryKey.setBookId(bookId);
                        userBookPrimaryKey.setUserId(userId);
                        Optional<UserBooks> optionalUserBooks = userBooksRepository.findById(userBookPrimaryKey);
                        if(optionalUserBooks.isPresent()) {
                            userBooks = optionalUserBooks.get();
                        } else {
                            userBooks = new UserBooks();
                        }

                        model.addAttribute("userBooks", userBooks);
                        model.addAttribute("loginId", principal.getAttribute("login"));
                        model.addAttribute("bookId", bookId);

                    }

                
            }

            return "book";
        

            }

    }
}
