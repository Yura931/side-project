package sideproject.boardservice.post.dto;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import sideproject.boardservice.common.enums.SortDirection;
import sideproject.boardservice.post.dto.request.SearchPostRequest;


public record SearchPostDto(
        Long boardId,
        SortDirection sortDirection,
        String title,
        String content,
        String createdBy,
        Pageable pageable
) {
    public static SearchPostDto of(Long boardId, SearchPostRequest request, PageRequest pageRequest) {
        return new SearchPostDto(
                boardId,
                request.getSortDirection(),
                request.getTitle(),
                request.getContent(),
                request.getCreatedBy(),
                pageRequest
        );
    }

    public static SearchPostDto of(Long boardId, SortDirection direction, String title, String content, String createdBy, Pageable pageable) {
        return new SearchPostDto(
                boardId,
                direction,
                title,
                content,
                createdBy,
                pageable
        );
    }
}
