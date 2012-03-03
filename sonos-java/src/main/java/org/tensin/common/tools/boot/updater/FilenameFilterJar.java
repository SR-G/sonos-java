package org.tensin.common.tools.boot.updater;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Filtre sur des noms de fichiers *.jar
 * 
 * @author Serge SIMON - u248663
 * @version $Revision: 1.2 $
 * @since Dec 22, 2007 4:46:54 PM
 */
public class FilenameFilterJar implements FilenameFilter {

    /** PATTERN indiquant "tous les jar". */
    private static final String PATTERN = ".+\\.jar$";

    /**
     * Constructeur.
     */
    public FilenameFilterJar() {
    }

    /**
     * Méthode d'acceptation.
     * 
     * @param dir
     *            Le répertoire en cours.
     * @param name
     *            Le nom du fichier en cours.
     * @return True si le fichier matche l'expression régulière courante, false sinon.
     * @see FilenameFilter#accept(java.io.File, java.lang.String)
     */
    public boolean accept(final File dir, final String name) {
        return name.matches(PATTERN);
    }
}
