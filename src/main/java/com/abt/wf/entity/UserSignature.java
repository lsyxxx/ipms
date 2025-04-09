package com.abt.wf.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "u_sig")
@NoArgsConstructor
@AllArgsConstructor
public class UserSignature {
    @Id
    @Size(max = 255)
    @Column(name = "id", nullable = false)
    private String id;

    @Size(max = 255)
    @Column(name = "job_number", unique = true, nullable = false)
    private String jobNumber;

    @Size(max = 32)
    @NotNull
    @Column(name = "user_name", nullable = false, length = 32)
    private String userName;

    @Size(max = 255)
    @Column(name = "file_name")
    private String fileName;

    @Size(max = 255)
    @Column(name = "user_id")
    private String userId;

    @Lob
    @Column(name = "base64")
    private String base64;

}