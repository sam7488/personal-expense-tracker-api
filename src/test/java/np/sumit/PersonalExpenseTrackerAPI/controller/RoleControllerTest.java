package np.sumit.PersonalExpenseTrackerAPI.controller;

import np.sumit.PersonalExpenseTrackerAPI.config.SecurityConfig;
import np.sumit.PersonalExpenseTrackerAPI.dto.request.RoleRequestDto;
import np.sumit.PersonalExpenseTrackerAPI.dto.response.RoleResponseDto;
import np.sumit.PersonalExpenseTrackerAPI.entity.ERole;
import np.sumit.PersonalExpenseTrackerAPI.service.CustomUserDetailsService;
import np.sumit.PersonalExpenseTrackerAPI.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RoleController.class)
@Import(SecurityConfig.class)
public class RoleControllerTest {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    RoleService roleService;

    @Test
    @WithMockUser(roles = "USER")
    void shouldRejectManipulatingRoleOtherThanADMIN() throws Exception {
        RoleRequestDto roleRequestDto = new RoleRequestDto(ERole.ROLE_USER);

        mockMvc.perform(
                        post("/api/roles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(roleRequestDto))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldThrowExceptionIfRoleIsNull() throws Exception {
        RoleRequestDto roleRequestDto = new RoleRequestDto(null);

        mockMvc.perform(
                        post("/api/roles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(roleRequestDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Role name is required"));

        verifyNoInteractions(roleService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAddRole() throws Exception {
        RoleRequestDto roleRequestDto = new RoleRequestDto(ERole.ROLE_USER);
        RoleResponseDto roleResponseDto = new RoleResponseDto(1L, roleRequestDto.getRole());

        when(roleService.createRole(any(RoleRequestDto.class)))
                .thenReturn(roleResponseDto);

        mockMvc.perform(
                        post("/api/roles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(roleRequestDto))
                )
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(roleResponseDto)));

        verify(roleService).createRole(any(RoleRequestDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetRoleById() throws Exception {
        Long roleId = 1L;
        RoleResponseDto roleResponseDto = new RoleResponseDto(1L, ERole.ROLE_USER);

        when(roleService.getRoleById(roleId))
                .thenReturn(roleResponseDto);

        mockMvc.perform(
                        get("/api/roles/{id}", roleId)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(roleResponseDto)));

        verify(roleService).getRoleById(eq(roleId));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetAllRoles() throws Exception {
        RoleResponseDto role1 = new RoleResponseDto(1L, ERole.ROLE_USER);
        RoleResponseDto role2 = new RoleResponseDto(1L, ERole.ROLE_USER);
        List<RoleResponseDto> roles = List.of(role1, role2);


        when(roleService.getAllRole())
                .thenReturn(roles);

        mockMvc.perform(
                        get("/api/roles")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(roles)));

        verify(roleService).getAllRole();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateRole() throws Exception {
        Long roleId = 1L;
        RoleRequestDto roleRequestDto = new RoleRequestDto(ERole.ROLE_USER);
        RoleResponseDto roleResponseDto = new RoleResponseDto(1L, roleRequestDto.getRole());

        when(roleService.updateRole(eq(roleId), any(RoleRequestDto.class)))
                .thenReturn(roleResponseDto);

        mockMvc.perform(
                        put("/api/roles")
                                .param("id", roleId.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(roleRequestDto))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(roleResponseDto)));

        verify(roleService).updateRole(eq(roleId), any(RoleRequestDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteRoleById() throws Exception {
        Long roleId = 1L;

        mockMvc.perform(
                delete("/api/roles")
                        .param("id", roleId.toString())
        )
                .andExpect(status().isNoContent());

        verify(roleService).deleteRoleById(roleId);
    }
}
