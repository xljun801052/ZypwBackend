package com.xlys.zypwhomepage.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Photo {

    private Integer pid;

    private String name;

    private String encodedName;

    private String path;

    private String extType;

    private Long size;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private Integer validFlag;

    private Integer creator;
}
