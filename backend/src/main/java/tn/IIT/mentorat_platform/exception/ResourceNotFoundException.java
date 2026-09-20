package tn.IIT.mentorat_platform.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, String field, Object value) {
        super(String.format("%s non trouvé(e) avec %s = '%s'", resourceName, field, value));
    }
}
