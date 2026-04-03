package com.LMS.Pulse.service;

import com.LMS.Pulse.model.Jotform;
import com.LMS.Pulse.model.JotformElement;
import com.LMS.Pulse.model.JotformPage;
import com.LMS.Pulse.repository.JotformRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JotformService {

    @Autowired
    private JotformRepository jotformRepository;

    /**
     * Creates and saves a new Jotform entity from a map of data.
     * This method is transactional, ensuring that the entire object graph (Jotform, pages, and elements)
     * is saved in a single transaction.
     *
     * @param data A map containing the Jotform data, typically from a JSON request body.
     * @return The saved Jotform entity with its generated ID.
     */
    @Transactional
    public Jotform saveJotform(Map<String, Object> data) {
        Jotform jotform = new Jotform();
        jotform.setJotformName((String) data.get("jotformName"));
        jotform.setTotalPages(getIntegerValue(data.get("totalPages")));

        List<Map<String, Object>> pagesData = (List<Map<String, Object>>) data.get("pages");
        if (pagesData != null) {
            for (Map<String, Object> pageData : pagesData) {
                JotformPage page = new JotformPage();
                page.setPage(getIntegerValue(pageData.get("page")));
                page.setTotalElements(getIntegerValue(pageData.get("totalElements")));
                page.setJotform(jotform); // Link back to the parent Jotform

                List<Map<String, Object>> elementsData = (List<Map<String, Object>>) pageData.get("elements");
                if (elementsData != null) {
                    for (Map<String, Object> elementData : elementsData) {
                        JotformElement element = new JotformElement();
                        element.setId(getLongValue(elementData.get("id")));
                        element.setTagName((String) elementData.get("tagName"));
                        element.setElementName((String) elementData.get("elementName"));
                        element.setContent((String) elementData.get("content"));
                        element.setSequence(getIntegerValue(elementData.get("sequence")));
                        element.setPage(page); // Link back to the parent page
                        page.getElements().add(element);
                    }
                }
                jotform.getPages().add(page);
            }
        }
        return jotformRepository.save(jotform);
    }

    /**
     * Deletes a Jotform by its ID.
     * Throws an EntityNotFoundException if no Jotform with the given ID exists.
     *
     * @param id The ID of the Jotform to delete.
     */
    @Transactional
    public void deleteJotformById(Long id) {
        if (!jotformRepository.existsById(id)) {
            throw new EntityNotFoundException("Jotform with ID " + id + " not found.");
        }
        jotformRepository.deleteById(id);
    }

    /**
     * Retrieves all Jotform entities from the database.
     *
     * @return A list of all Jotforms.
     */
    public List<Jotform> getAllJotforms() {
        return jotformRepository.findAll();
    }

    /**
     * Retrieves a distinct list of all Jotform names.
     *
     * @return A list of unique Jotform names.
     */
    public List<String> getAllJotformNames() {
        return jotformRepository.findAll().stream()
                .map(Jotform::getJotformName)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * A sample method to retrieve a hardcoded Jotform (e.g., with ID 1).
     * Used for testing or specific use cases.
     *
     * @return The Jotform with ID 1, or null if not found.
     */
    public Jotform getreact() {
        return jotformRepository.findById(101L).orElse(null);
    }

    // --- Private Helper Methods ---

    /**
     * Safely converts an object to an Integer.
     *
     * @param obj The object to convert.
     * @return The integer value, or null if the object is not a number.
     */
    private Integer getIntegerValue(Object obj) {
        if (obj instanceof Number) {
            return ((Number) obj).intValue();
        }
        return null;
    }

    /**
     * Safely converts an object to a Long.
     *
     * @param obj The object to convert.
     * @return The long value, or null if the object is not a number.
     */
    private Long getLongValue(Object obj) {
        if (obj instanceof Number) {
            return ((Number) obj).longValue();
        }
        return null;
    }
}
