package subproject.admin.board.dto.item;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import subproject.admin.board.entity.BoardCategory;
import subproject.admin.board.entity.Board;
import subproject.admin.board.entity.enums.Enabled;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class BoardItem {
    private final UUID id;
    private final Enabled boardEnabled;
    private final Enabled boardVisible;
    private final String boardType;
    private final String boardTitle;
    private final String boardDescription;
    private final Enabled boardCategoryEnabled;
    private final List<BoardCategory> boardCategory;
    private final Enabled boardFileEnabled;
    private final Enabled boardCommentEnabled;
    private final Enabled boardReplyCommentEnabled;

    public static BoardItem boardEntityToDto(Board board) {
        return new BoardItem(
                board.getId(),
                board.getBoardEnabled(),
                board.getBoardVisible(),
                board.getBoardType(),
                board.getBoardTitle(),
                board.getBoardDescription(),
                board.getBoardCategoryEnabled(),
                board.getBoardCategory(),
                board.getBoardFileEnabled(),
                board.getBoardCommentEnabled(),
                board.getBoardReplyCommentEnabled()
        );
    }
}