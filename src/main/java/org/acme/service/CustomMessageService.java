package org.acme.service;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import org.acme.dto.MessageDTO;
import org.acme.model.Message;
import org.eclipse.microprofile.metrics.annotation.SimplyTimed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@ApplicationScoped
public class CustomMessageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomMessageService.class);

    @Inject
    EntityManager em;

    @Transactional
    @Counted(value = "database.calls.add", extraTags = {"db", "language", "db", "content"})
    public void createMessage(MessageDTO dto) {
        Message message = new Message();
        message.setLocale(dto.getLocale());
        message.setContent(dto.getContent());
        em.persist(message);
    }

    @Counted(value = "database.calls.find", extraTags = {"db", "content"})
    public List<Message> findMessages(String content) {
        TypedQuery<Message> query = em.createQuery(
                "SELECT m from Message m WHERE m.content = :content", Message.class).
                setParameter("content", content);
        return query.getResultList();
    }

    @Timed(value = "database.calls.filter", longTask = true, extraTags = {"db", "language"})
    public List<Message> filterMessages(Locale locale) {
        TypedQuery<Message> query = em.createQuery(
                "SELECT m from Message m WHERE m.locale = :locale", Message.class).
                setParameter("locale", locale);
        return query.getResultList();
    }

    @Counted(value = "database.calls.get", extraTags = {"db", "all"})
    public List<Message> findAll() {
        TypedQuery<Message> query = em.createQuery("SELECT m from Message m", Message.class);
        List<Message> messages = new ArrayList<>();
        try {
            messages = query.getResultList();
        } catch (NoResultException e) {
            LOGGER.error("No messages present in the database", e);
        }
        return messages;
    }

    public List<Message> search(List<String> content) {
        TypedQuery<Message> query = em.createQuery(
                "SELECT m from Message m WHERE m.content in :content", Message.class).
                setParameter("content", content);
        return query.getResultList();
    }
}
