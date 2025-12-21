package com.helpie.backend.controller;

import com.helpie.backend.common.fixtures.AuthFixtures;
import com.helpie.backend.common.fixtures.GroupFixtures;
import com.helpie.backend.service.file.FileService;
import com.helpie.backend.service.group.GroupService;

import com.helpie.backend.support.ConnectionSnapshot;
import com.helpie.backend.support.ConnectionUtils;

import java.util.List;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
public class ConnectionTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoSpyBean
    ConnectionUtils connectionUtils;

    @MockitoBean
    GroupService groupService;

    @MockitoBean
    FileService fileService;

    @Test
    void after_findById_connection_is_returned_to_pool() throws Exception {

        given(fileService.uploadFiles(any())).willReturn(List.of());
        given(groupService.createGroup(anyLong(), any(), anyList(), any()))
            .willReturn(GroupFixtures.CREATE_RESPONSE);

        mockMvc.perform(
                multipart("/api/v1/group/create")
                    .with(authentication(AuthFixtures.authentication()))
                    .with(GroupFixtures.validParams())
            )
            .andExpect(status().isOk());


        ArgumentCaptor<ConnectionSnapshot> captor = ArgumentCaptor.forClass(ConnectionSnapshot.class);
        verify(connectionUtils, times(1)).onAfterCityLookup(captor.capture());

        assertThat(captor.getValue().active()).isEqualTo(0);

    }



}

