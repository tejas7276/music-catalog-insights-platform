package com.musiccatalog.controller;

import com.musiccatalog.dto.response.SearchResponse;
import com.musiccatalog.service.ItunesSearchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Validated
@Tag(name = "Search", description = "Proxy for the iTunes Search API - the frontend never calls Apple directly")
public class SearchController {

    private final ItunesSearchService itunesSearchService;

    @GetMapping
    public ResponseEntity<SearchResponse> search(
            @RequestParam @NotBlank(message = "query must not be blank") String query,
            @RequestParam(defaultValue = "song") String type
    ) {
        return ResponseEntity.ok(itunesSearchService.searchSongs(query));
    }
}
