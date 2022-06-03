package org.acme.service;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.quarkus.logging.Log;
import org.acme.dto.MessageDTO;
import org.acme.model.Message;

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

    @Inject
    EntityManager em;

    @Transactional
    @Counted(value = "persistency.calls.add", extraTags = {"db", "language", "db", "content"})
    public void createMessage(MessageDTO dto) {
        Message message = new Message();
        message.setLocale(dto.getLocale());
        message.setContent(dto.getContent());
        em.persist(message);
    }

    @Counted(value = "persistency.calls.find", extraTags = {"db", "content"})
    public List<Message> findMessages(String content) {
        TypedQuery<Message> query = em.createQuery(
                "SELECT m from Message m WHERE m.content = :content", Message.class).
                setParameter("content", content);
        return query.getResultList();
    }

    @Timed(value = "persistency.calls.filter", longTask = true, extraTags = {"db", "language"})
    public List<Message> filterMessages(Locale locale) {
        TypedQuery<Message> query = em.createQuery(
                "SELECT m from Message m WHERE m.locale = :locale", Message.class).
                setParameter("locale", locale);
        return query.getResultList();
    }

    @Counted(value = "persistency.calls.get", extraTags = {"db", "all"})
    public List<Message> findAll() {
        TypedQuery<Message> query = em.createQuery("SELECT m from Message m", Message.class);
        List<Message> messages = new ArrayList<>();
        try {
            messages = query.getResultList();
        } catch (NoResultException e) {
            Log.errorf(e, "No messages present in the database");
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
