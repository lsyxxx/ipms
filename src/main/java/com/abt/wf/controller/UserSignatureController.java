package com.abt.wf.controller;

import com.abt.common.model.R;
import com.abt.wf.entity.UserSignature;
import com.abt.wf.service.UserSignatureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *
 */
@RestController
@Slf4j
@RequestMapping("/us")
public class UserSignatureController {

    private final UserSignatureService userSignatureService;

    public UserSignatureController(UserSignatureService userSignatureService) {
        this.userSignatureService = userSignatureService;
    }

    //    @Secured("U_SIG_VIEW")
    @GetMapping("/find/all")
    public R<List<UserSignature>> getAllSignatures() {
        final List<UserSignature> list = userSignatureService.getAllUserSignatures();
        return R.success(list);
    }


}
