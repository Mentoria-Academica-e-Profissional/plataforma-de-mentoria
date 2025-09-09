package br.edu.ufape.plataforma.mentoria.mapper;

import br.edu.ufape.plataforma.mentoria.dto.SessionDTO;
import br.edu.ufape.plataforma.mentoria.enums.Status;
import br.edu.ufape.plataforma.mentoria.model.Session;
import org.springframework.stereotype.Component;

@Component
public class SessionMapper {

     public Session toEntity(SessionDTO sessionDTO) {
         if (sessionDTO == null) {
             return null;
         }
         Session session = new Session();
         session.setDate(sessionDTO.getDate());
         session.setTime(sessionDTO.getTime());
         session.setMeetingTopic(sessionDTO.getMeetingTopic());
         session.setStatus(Status.PENDING);
         session.setLocation(sessionDTO.getLocation());
         return session;
     }

     public SessionDTO toDTO(Session session) {
         if (session == null) {
             return null;
         }
         SessionDTO sessionDTO = new SessionDTO();
         sessionDTO.setId(session.getId());
         sessionDTO.setMentorId(session.getMentor().getId());
         sessionDTO.setMentoredId(session.getMentored().getId());
         sessionDTO.setDate(session.getDate());
         sessionDTO.setTime(session.getTime());
         sessionDTO.setMeetingTopic(session.getMeetingTopic());
         sessionDTO.setStatus(session.getStatus());
         sessionDTO.setLocation(session.getLocation());
         return sessionDTO;
     }
}
