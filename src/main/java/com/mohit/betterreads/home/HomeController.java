package com.mohit.betterreads.home;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.mohit.betterreads.user.BooksByUser;
import com.mohit.betterreads.user.BooksByUserRepository;



@Controller
public class HomeController {
    private final String ROOT_URL = "http://covers.openlibrary.org/b/id/";

    
    @Autowired
    private BooksByUserRepository booksByUserRepository;

    @GetMapping("/")
    public String home(@AuthenticationPrincipal OAuth2User principal, Model model) {
       

        if(principal == null || principal.getAttribute("login") == null) {
            return "index";
        } else {
            String userId = principal.getAttribute("login");
            Slice<BooksByUser> bookSlice = booksByUserRepository.findAllById(userId, CassandraPageRequest.of(0, 100));
            List<BooksByUser> bookByUser = bookSlice.getContent().stream().distinct().map(book -> {
                if(book.getCoverIds() != null && book.getCoverIds().size() > 0) {
                    book.setCoverUrl(userId);
                    book.setCoverUrl(ROOT_URL + book.getCoverIds().get(0)  + "-M.jpg");
                    book.setReadingStatus(book.getReadingStatus().substring(2));
                    System.out.println(book.getReadingStatus());
                }

                return book;
            }).collect(Collectors.toList());
            model.addAttribute("booksByUser", bookByUser);
            System.out.println(bookByUser);    
            return "home";
        }


        
    }
    
}
