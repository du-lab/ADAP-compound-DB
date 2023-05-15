package org.dulab.adapcompounddb.site.services;

import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;

import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.junit.*;
import org.junit.Assert.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.junit.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;


@RunWith(MockitoJUnitRunner.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    public void testSendOrganizationInviteEmail() throws MessagingException {
        UserPrincipal user = new UserPrincipal();
        user.setEmail("user@example.com");
        UserPrincipal organizationUser = new UserPrincipal();
        organizationUser.setEmail("org@example.com");
        String url = "https://adap.cloud/organization/addUser?token=abc123&orgEmail=org@example.com";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        doNothing().when(mailSender).send(mimeMessage);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendOrganizationInviteEmail(user, organizationUser, url);

        verify(mailSender).createMimeMessage();

        ArgumentCaptor<MimeMessage> mimeMessageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(mimeMessageCaptor.capture());
    }
}

