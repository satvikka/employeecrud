package com.satvika.employeecrud.pojo;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentResponsePojo {

    private String fileName;
    private String fileUrl;

}
