package gh.marad.chi.language.runtime.namespaces;

public class NoSuchPackageException extends RuntimeException {
    public NoSuchPackageException(String packageName) {
        super("Package %s does not exist".formatted(packageName));
    }
}
