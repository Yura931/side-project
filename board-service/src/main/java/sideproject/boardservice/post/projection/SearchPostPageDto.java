package sideproject.boardservice.post.projection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString(of = { "id", "categoryName", "postTitle", "postContent", "createDate", "createBy"})
public class SearchPostPageDto {
    private UUID id;
    private String categoryName;
    private String postTitle;
    private String postContent;
    private LocalDateTime createDate;
    private String createBy;
}
