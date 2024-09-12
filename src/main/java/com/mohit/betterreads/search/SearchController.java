package com.mohit.betterreads.search;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class SearchController {
    private final String ROOT_URL = "http://covers.openlibrary.org/b/id/";
    private final WebClient webClient;
    public SearchController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.exchangeStrategies(ExchangeStrategies.builder()
        .codecs(configurer -> configurer
                  .defaultCodecs()
                  .maxInMemorySize(16 * 1024 * 1024))
                .build()).baseUrl("http://openlibrary.org/search.json").build();
    
        }
    

  
    @GetMapping("/search")
    public String  getSerarchResults(@RequestParam String query, Model model, @AuthenticationPrincipal OAuth2User principal) {
        
        Mono<SearchResult> foo = webClient.get()
            .uri("?q={query}", query)
            .retrieve().bodyToMono(SearchResult.class);
            //System.out.println(query);
        SearchResult result = foo.block();
        List<SearchResultBook> books = result.getDocs()
            .stream().limit(30).map(bookResult -> {
                bookResult.setKey(bookResult.getKey().replace("/works/", ""));
                String coverId = bookResult.getCover_i();
                if(StringUtils.hasText(coverId)) {
                    coverId = ROOT_URL+ coverId + "-M.jpg";
                } else {
                    coverId = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ac/No_image_available.svg/600px-No_image_available.svg.png";
                }
                //System.out.println(bookResult.getKey());
                bookResult.setCover_i(coverId);

                return bookResult;
            })
            .collect(Collectors.toList());


        
        if(principal != null && principal.getAttribute("login") != null)model.addAttribute("loginId", principal.getAttribute("login"));
        model.addAttribute("searchResults", books);
            return "search";
    }
}
