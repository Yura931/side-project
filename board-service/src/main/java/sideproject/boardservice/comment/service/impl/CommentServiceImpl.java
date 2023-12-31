package sideproject.boardservice.comment.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sideproject.boardservice.comment.dto.RegisterCommentDto;
import sideproject.boardservice.comment.dto.item.RegisterCommentItem;
import sideproject.boardservice.comment.dto.response.RegisterCommentResponse;
import sideproject.boardservice.comment.entity.Comment;
import sideproject.boardservice.comment.repository.CommentRepository;
import sideproject.boardservice.comment.service.CommentService;
import sideproject.boardservice.post.entity.Post;
import sideproject.boardservice.post.repository.PostRepository;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public RegisterCommentResponse save(RegisterCommentDto dto) {
        Post post = postRepository.findById(dto.postId())
                .orElseThrow(EntityNotFoundException::new);
        Comment comment = Comment.createComment(dto, post);
        Comment saveComment = commentRepository.save(comment);
        RegisterCommentItem registerCommentItem = RegisterCommentItem.CommentEntityToDto(saveComment);
        return new RegisterCommentResponse(registerCommentItem);
    }
}
