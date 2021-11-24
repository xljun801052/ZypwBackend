package com.xlys.zypwhomepage.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    private Integer id;

    private Integer opusId;

    private Integer parentId;

    private String content;

    private String imgLinks;

    private Integer commentOwner;

    private Integer anonymousFlag;

    private Integer favoriteCount;

    private Integer replyCount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime commentTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifyTime;

}
