package sideproject.boardservice.post.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import sideproject.boardservice.board.entity.Board;
import sideproject.boardservice.board.entity.BoardCategory;
import sideproject.boardservice.common.entity.BaseEntity;
import sideproject.boardservice.post.dto.RegisterPostDto;
import sideproject.boardservice.post.dto.UpdatePostDto;

import java.util.UUID;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "file_mapping_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = { "id", "postTitle", "postContent", "viewCount"})
public class Post extends BaseEntity {

    @Id
    @Column(name = "post_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    @JsonIgnore
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_category_id")
    @JsonIgnore
    private BoardCategory boardCategory;

    private String postTitle;

    private String postContent;

    @ColumnDefault("0")
    private Long viewCount;

    @Column(name = "file_mapping_id")
    private UUID fileMappingId;

    private Post(UUID id, Board board, BoardCategory category, String postTitle, String postContent, Long viewCount) {
        this.id = id;
        this.board = board;
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.viewCount = viewCount;
        this.boardCategory = category;
    }

    public static Post createPost(RegisterPostDto dto, Board board, BoardCategory boardCategory) {
        UUID id = UUID.randomUUID();
        return new Post(
                id,
                board,
                boardCategory,
                dto.postTitle(),
                dto.postContent(),
                dto.viewCount()
        );
    }

    public void addFileMappingId(UUID fileMappingId) {
        this.fileMappingId = fileMappingId;
    }

    public void updatePost(UpdatePostDto dto) {
        this.postTitle = dto.postTitle();
        this.postContent = dto.postContent();
        this.boardCategory.updateCategory(dto.categoryName());
    }

    public void updateViewCount() {
        this.viewCount = this.viewCount + 1;
    }

}
