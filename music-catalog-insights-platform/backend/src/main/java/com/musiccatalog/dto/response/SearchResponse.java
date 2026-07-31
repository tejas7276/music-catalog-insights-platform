package com.musiccatalog.dto.response;

import java.util.List;

public record SearchResponse(
        String query,
        int resultCount,
        List<SongSearchResultResponse> results
) {}
