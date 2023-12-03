package subproject.admin.board.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import subproject.admin.board.dto.RegisterBoardDto;
import subproject.admin.board.dto.request.RegisterBoardRequest;
import subproject.admin.board.entity.enums.Enabled;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Rollback(value = false)
@Transactional
class BoardTest {

    @PersistenceContext
    EntityManager em;

    @BeforeEach
    public void beforeEach() {

        RegisterBoardDto registerBoardDto = RegisterBoardDto.from(
                new RegisterBoardRequest(
                        Enabled.Y,
                        Enabled.Y,
                        "A",
                        "title",
                        "description",
                        Enabled.Y,
                        Enabled.Y,
                        Enabled.Y,
                        Enabled.Y,
                        Arrays.asList("category1", "category2")
                )
        );
        Board board = Board.createBoard(registerBoardDto);
        em.persist(board);
    }

    @Test
    public void boardInsert() throws Exception {
        RegisterBoardDto boardMasterCommand = RegisterBoardDto.from(
                new RegisterBoardRequest(
                        Enabled.Y,
                        Enabled.Y,
                        "A",
                        "title",
                        "description",
                        Enabled.Y,
                        Enabled.Y,
                        Enabled.Y,
                        Enabled.Y,
                        Arrays.asList()
                )
        );
        Board board = Board.createBoard(boardMasterCommand);


        em.persist(board);

        Board findBoard = em.find(Board.class, board.getId());

        assertThat(board.getId()).isEqualTo(findBoard.getId());
        assertThat(board.getBoardCategory().size()).isEqualTo(2);
    }

    @Test
    public void boardUpdateTest() {
        List<Board> bm = em.createQuery("select bm from Board bm", Board.class)
                .getResultList();

        UUID findId = bm.get(0).getId();
        Board findBoard = em.find(Board.class, findId);
        findBoard.updateBoardMaster(
                Enabled.Y,
                Enabled.Y,
                "A",
                "title2",
                "description2",
                Enabled.Y,
                Enabled.Y,
                Enabled.Y, Enabled.Y);
        assertThat(findBoard.getBoardTitle()).isEqualTo("title2");
    }
}