package src.repository;

import src.model.Applicant;
import java.util.List;

public interface ApplicantRepository {
    List<Applicant> loadAll();
    void update(Applicant applicant);
    void save(Applicant applicant);
    boolean exists(String nric);
}
