package org.tensin.common.tools.boot.updater;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

/**
 * JarContainer.
 * 
 * @author u248663
 * @version $Revision: 1.2 $
 * @since 18 mars 2010 13:10:52
 */
public class JarContainer implements Comparable<JarContainer> {

    /** mavenGroupIdDatabase. */
    private static Map<String, String> mavenGroupIdDatabase;

    /** MODE_BASENAME : regexp of the base name. */
    public static final String MODE_BASENAME = "BASENAME";

    /** MODE_FULLNAME : full name and version of the jar expected !. */
    public static final String MODE_FULLNAME = "FULLNAME";

    /** mode. */
    private static String mode = MODE_BASENAME;

    /**
     * Méthode findJar.
     * 
     * @param jars
     *            the jars
     * @param jarToMatch
     *            the jar to match
     * @return JarContainer
     */
    private static JarContainer findJar(final Collection<JarContainer> jars, final JarContainer jarToMatch) {
        for (final JarContainer jar : jars) {
            if (jar.getArtifactId().equalsIgnoreCase(jarToMatch.getArtifactId()) && jar.getVersion().equalsIgnoreCase(jarToMatch.getVersion())
                    && (jar != jarToMatch)) {
                return jar;
            }
        }
        return null;
    }

    /**
     * Méthode fusion.
     * 
     * @param jars
     *            the jars
     * @return Collection<JarContainer>
     */
    public static Collection<JarContainer> fusion(final Collection<JarContainer> jars) {
        final Collection<JarContainer> merged = new ArrayList<JarContainer>();
        final Collection<JarContainer> processed = new ArrayList<JarContainer>();
        for (final JarContainer jar : jars) {
            if (!processed.contains(jar)) {
                final JarContainer similijar = findJar(merged, jar);
                if (jar != similijar) {
                    if (similijar == null) {
                        merged.add(jar);
                    } else {
                        similijar.fusion(jar);
                    }
                    processed.add(jar);
                }
            }
        }
        return merged;

    }

    /**
     * Méthode.
     * 
     * @return the maven group id database
     */
    public static Map<String, String> getMavenGroupIdDatabase() {
        if (mavenGroupIdDatabase == null) {
            mavenGroupIdDatabase = new HashMap<String, String>();
            mavenGroupIdDatabase.put("antlr", "antlr");
            mavenGroupIdDatabase.put("aopalliance", "aopalliance");
            mavenGroupIdDatabase.put("asm", "asm");
            mavenGroupIdDatabase.put("asm-attrs", "asm");
            mavenGroupIdDatabase.put("backport-util-concurrent", "backport-util-concurrent");
            mavenGroupIdDatabase.put("batik-awt-util", "batik");
            mavenGroupIdDatabase.put("batik-dom", "batik");
            mavenGroupIdDatabase.put("batik-svggen", "batik");
            mavenGroupIdDatabase.put("bsh", "bsh");
            mavenGroupIdDatabase.put("castor", "castor");
            mavenGroupIdDatabase.put("cglib", "cglib");
            mavenGroupIdDatabase.put("maxq", "com.bitmechanic.maxq");
            mavenGroupIdDatabase.put("h2", "com.h2database");
            mavenGroupIdDatabase.put("jmep", "com.iabcinc.jmep");
            mavenGroupIdDatabase.put("icu4j", "com.ibm.icu");
            mavenGroupIdDatabase.put("cdn", "com.inetpsa.cdn00");
            mavenGroupIdDatabase.put("clppkannuldap", "com.inetpsa.clp");
            mavenGroupIdDatabase.put("ddl-core", "com.inetpsa.ddl00");
            mavenGroupIdDatabase.put("ddl-java", "com.inetpsa.ddl00");
            mavenGroupIdDatabase.put("ecw-core", "com.inetpsa.ecw00");
            mavenGroupIdDatabase.put("ecw-struts", "com.inetpsa.ecw00");
            mavenGroupIdDatabase.put("elf-java", "com.inetpsa.elf00");
            mavenGroupIdDatabase.put("fwk-commons", "com.inetpsa.fwk");
            mavenGroupIdDatabase.put("fwk-core", "com.inetpsa.fwk");
            mavenGroupIdDatabase.put("fwk-i18n", "com.inetpsa.fwk");
            mavenGroupIdDatabase.put("fwk-i18n-db", "com.inetpsa.fwk");
            mavenGroupIdDatabase.put("fwk-i18n-hibernate", "com.inetpsa.fwk");
            mavenGroupIdDatabase.put("fwk-noyau", "com.inetpsa.fwk");
            mavenGroupIdDatabase.put("fwk-struts", "com.inetpsa.fwk");
            mavenGroupIdDatabase.put("gan", "com.inetpsa.gan00");
            mavenGroupIdDatabase.put("ltp-java", "com.inetpsa.ltp00");
            mavenGroupIdDatabase.put("ltp-start", "com.inetpsa.ltp00");
            mavenGroupIdDatabase.put("ltp-strutsv1", "com.inetpsa.ltp00");
            mavenGroupIdDatabase.put("mqp", "com.inetpsa.mqp00");
            mavenGroupIdDatabase.put("tbf-common", "com.inetpsa.tbf00");
            mavenGroupIdDatabase.put("tbf-telnet", "com.inetpsa.tbf00");
            mavenGroupIdDatabase.put("tbf-txcom", "com.inetpsa.tbf00");
            mavenGroupIdDatabase.put("itext", "com.lowagie");
            mavenGroupIdDatabase.put("commons-beanutils", "commons-beanutils");
            mavenGroupIdDatabase.put("commons-betwixt", "commons-betwixt");
            mavenGroupIdDatabase.put("commons-cache", " commons-cache");
            mavenGroupIdDatabase.put("commons-cli", "commons-cli");
            mavenGroupIdDatabase.put("commons-codec", "commons-codec");
            mavenGroupIdDatabase.put("commons-collections", "commons-collections");
            mavenGroupIdDatabase.put("commons-configuration", "commons-configuration");
            mavenGroupIdDatabase.put("commons-dbcp", "commons-dbcp");
            mavenGroupIdDatabase.put("commons-digester", "commons-digester");
            mavenGroupIdDatabase.put("commons-email", "commons-email");
            mavenGroupIdDatabase.put("commons-fileupload", "commons-fileupload");
            mavenGroupIdDatabase.put("commons-httpclient", "commons-httpclient");
            mavenGroupIdDatabase.put("commons-io", "commons-io");
            mavenGroupIdDatabase.put("commons-jxpath", "commons-jxpath");
            mavenGroupIdDatabase.put("commons-lang", "commons-lang");
            mavenGroupIdDatabase.put("commons-logging", "commons-logging");
            mavenGroupIdDatabase.put("commons-logging-api", "commons-logging");
            mavenGroupIdDatabase.put("commons-net", "commons-net");
            mavenGroupIdDatabase.put("commons-pool", "commons-pool");
            mavenGroupIdDatabase.put("commons-validator", "commons-validator");
            mavenGroupIdDatabase.put("dom4j", "dom4j");
            mavenGroupIdDatabase.put("gnu-regexp", "gnu-regexp");
            mavenGroupIdDatabase.put("hsqldb", "hsqldb");
            mavenGroupIdDatabase.put("activation", "javax.activation");
            mavenGroupIdDatabase.put("mail", "javax.mail");
            mavenGroupIdDatabase.put("jmxremote", "javax.management");
            mavenGroupIdDatabase.put("jmxremote_optional", "javax.management");
            mavenGroupIdDatabase.put("jmxri", "javax.management");
            mavenGroupIdDatabase.put("jta", "javax.transaction");
            mavenGroupIdDatabase.put("jaxen", "jaxen");
            mavenGroupIdDatabase.put("jdom", "jdom");
            mavenGroupIdDatabase.put("jcommon", "jfree");
            mavenGroupIdDatabase.put("jfreechart", "jfree");
            mavenGroupIdDatabase.put("azzurri_clay_core", "jp.azzurri");
            mavenGroupIdDatabase.put("junit", "junit");
            mavenGroupIdDatabase.put("jython", "jython");
            mavenGroupIdDatabase.put("log4j", "log4j");
            mavenGroupIdDatabase.put("menu", "menu");
            mavenGroupIdDatabase.put("jericho-html", "net.htmlparser");
            mavenGroupIdDatabase.put("ehcache", "net.sf.ehcache");
            mavenGroupIdDatabase.put("jsr107cache", "net.sf.jsr107cache");
            mavenGroupIdDatabase.put("opencsv", "net.sf.opencsv");
            mavenGroupIdDatabase.put("db-ojb", "ojb");
            mavenGroupIdDatabase.put("quartz", "opensymphony");
            mavenGroupIdDatabase.put("ojdbc14", "oracle.jdbc-driver");
            mavenGroupIdDatabase.put("ant", "org.apache.ant");
            mavenGroupIdDatabase.put("ant-launcher", "org.apache.ant");
            mavenGroupIdDatabase.put("axis2", "org.apache.axis2");
            mavenGroupIdDatabase.put("aspectjweaver", "org.aspectj");
            mavenGroupIdDatabase.put("wstx-asl", "org.codehaus.woodstox");
            mavenGroupIdDatabase.put("hibernate", "org.hibernate");
            mavenGroupIdDatabase.put("jtidy", "org.hibernate");
            mavenGroupIdDatabase.put("azzurri_clay_core", "jp.azzurri");
            mavenGroupIdDatabase.put("spring-web", "org.springframework");
            mavenGroupIdDatabase.put("spring-security-taglibs", "org.springframework.security");
            mavenGroupIdDatabase.put("spring-security-acl", "org.springframework.security");
            mavenGroupIdDatabase.put("", "");
            mavenGroupIdDatabase.put("", "");
            mavenGroupIdDatabase.put("", "");
            mavenGroupIdDatabase.put("slf4j-api", "org.slf4j");
            mavenGroupIdDatabase.put("slf4j-log4j12", "org.slf4j");
            mavenGroupIdDatabase.put("spring-security-core", "org.springframework.security");
            mavenGroupIdDatabase.put("spring-aop", "org.springframework");
            mavenGroupIdDatabase.put("spring-beans", "org.springframework");
            mavenGroupIdDatabase.put("spring-context", "org.springframework");
            mavenGroupIdDatabase.put("spring-context-support", "org.springframework");
            mavenGroupIdDatabase.put("spring-core", "org.springframework");
            mavenGroupIdDatabase.put("spring-jdbc", "org.springframework");
            mavenGroupIdDatabase.put("spring-orm", "org.springframework");
            mavenGroupIdDatabase.put("spring-tx", "org.springframework");
            mavenGroupIdDatabase.put("oro", "oro");
            mavenGroupIdDatabase.put("poi", "poi");
            mavenGroupIdDatabase.put("stax", "stax");
            mavenGroupIdDatabase.put("stax-api", "stax");
            mavenGroupIdDatabase.put("struts-layout-psa", "struts-layout");
            mavenGroupIdDatabase.put("struts", "struts");
            mavenGroupIdDatabase.put("java-getopt", "urbanophile");
            mavenGroupIdDatabase.put("velocity", "velocity");
            mavenGroupIdDatabase.put("xalan", "xalan");
            mavenGroupIdDatabase.put("xercesImpl", "xerces");
            mavenGroupIdDatabase.put("xmlParserAPIs", "xerces");
            mavenGroupIdDatabase.put("xml-apis", "xml-apis");
            mavenGroupIdDatabase.put("xom", "xom");
        }
        return mavenGroupIdDatabase;
    }

    /**
     * Getter de l'attribut mode.
     * 
     * @return Returns L'attribut mode.
     */
    public static String getMode() {
        return mode;
    }

    /**
     * Détermine si une chaîne est alphabétique ou non [Az].
     * 
     * @param str
     *            La chaîne
     * @return boolean
     */
    public static boolean isAlpha(final String str) {
        if (str == null) {
            return false;
        }
        final int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isLetter(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Setter de l'attribut mode.
     * 
     * @param mode
     *            L'attribut mode.
     */
    public static void setMode(final String mode) {
        JarContainer.mode = mode;
    }

    /** origin. */
    private Collection<String> origins = new TreeSet<String>();

    /** description. */
    private String description = "";

    /** actif. */
    private boolean actif = true;

    /** destination. */
    private Collection<String> destinations = new TreeSet<String>();

    /** modules. */
    private Collection<String> modules = new TreeSet<String>();

    /** key. */
    private String key = "";

    /** groupId. */
    private String groupId = "";

    /** artifactId. */
    private String artifactId = "";

    /** version. */
    private String version = "";

    /** jarName. */
    private String jarName = "";

    /** startConstantsKey. */
    private String startConstantsKey = "";

    /** startConstantsRegExp. */
    private String startConstantsRegExp = "";

    /** cksum. */
    private int cksum = 0;

    /** path. */
    private String path = "";

    /**
     * Constructeur.
     */
    public JarContainer() {
        super();
    }

    /**
     * Constructeur.
     * 
     * @param origin
     *            the origin
     */
    public JarContainer(final Class<?> origin) {
        super();
        addOrigin(origin);
    }

    /**
     * Constructeur.
     * 
     * @param groupId
     *            the group id
     * @param artifactId
     *            the artifact id
     * @param version
     *            the version
     */
    public JarContainer(final String groupId, final String artifactId, final String version) {
        super();
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    /**
     * Méthode addDestination.
     * 
     * @param destination
     *            void
     */
    public void addDestination(final String destination) {
        destinations.add(destination);
    }

    /**
     * Méthode addOrigin.
     * 
     * @param origin
     *            void
     */
    public void addOrigin(final Class<?> origin) {
        origins.add(buildOriginClassName(origin));
    }

    /**
     * Adds the origins.
     * 
     * @param otherOrigins
     *            the other origins
     */
    public void addOrigins(final Collection<String> otherOrigins) {
        for (String otherOrigin : otherOrigins) {
            if (!origins.contains(otherOrigin)) {
                origins.add(otherOrigin);
            }
        }
    }

    /**
     * Méthode buildOriginClassName.
     * 
     * @param origin
     *            the origin
     * @return String
     */
    private String buildOriginClassName(final Class<?> origin) {
        final String className = origin.getName();
        return className.substring(className.lastIndexOf(".") + 1, className.length());
    }

    /**
     * Méthode.
     * 
     * @return the string
     */
    /*
     * private String extractGroupId(final String jarName) { final String
     * artifactId = extractArtifactId(jarName); final String groupId =
     * getMavenGroupIdDatabase().get(artifactId); if
     * (PyramideLauncherUtil.isEmpty(groupId)) { addUnboundGroupId(artifactId);
     * return ""; } else { return groupId; } }
     */

    public String buildStartConstantsRegExp() {
        return getJarBaseName() + ".*\\.jar";
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Comparable#compareTo(T)
     */
    /** {@inheritDoc}
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final JarContainer arg0) {
        return getJarName().compareTo((arg0).getJarName());
    }

    /**
     * Méthode.
     * 
     * @param jarName
     *            the jar name
     * @return the string
     */
    private String extractArtifactId(final String jarName) {
        final String artifactId = getJarBaseName();
        return artifactId;
    }

    /**
     * Méthode.
     * 
     * @param jarName
     *            the jar name
     * @return the string
     */
    private String extractVersion(final String jarName) {
        final String jarBaseName = getJarBaseName();
        String version = jarName.replaceAll(jarBaseName, "").replaceAll(".jar", "");
        if (version.startsWith("-")) {
            version = version.substring(1, version.length());
        }
        return version;
    }

    /**
     * Méthode fusion.
     * 
     * @param similijar
     *            the similijar
     * @return JarContainer
     */
    private JarContainer fusion(final JarContainer similijar) {
        if (StringUtils.isEmpty(key)) {
            setKey(similijar.getKey());
        }
        if (StringUtils.isEmpty(path)) {
            setPath(similijar.getPath());
        }
        if (cksum == 0) {
            cksum = similijar.getCksum();
        }
        if (StringUtils.isEmpty(startConstantsRegExp)) {
            setStartConstantsRegExp(similijar.getStartConstantsRegExp());
        }
        if (StringUtils.isEmpty(startConstantsKey)) {
            setStartConstantsKey(similijar.getStartConstantsKey());
        }
        if (StringUtils.isEmpty(jarName)) {
            setJarName(similijar.getJarName());
        }
        if (StringUtils.isEmpty(groupId)) {
            setGroupId(similijar.getGroupId());
        }
        if (StringUtils.isEmpty(artifactId)) {
            setArtifactId(similijar.getArtifactId());
        }
        if (StringUtils.isEmpty(version)) {
            setVersion(similijar.getVersion());
        }
        if (StringUtils.isEmpty(description)) {
            setDescription(similijar.getDescription());
        }
        for (final String destination : similijar.getDestinationsList()) {
            destinations.add(destination);
        }
        for (final String module : similijar.getModulesList()) {
            modules.add(module);
        }
        for (final String origin : similijar.getOrigins()) {
            origins.add(origin);
        }

        return this;
    }

    /**
     * Getter de l'attribut artifactId.
     * 
     * @return Returns L'attribut artifactId.
     */
    public String getArtifactId() {
        return artifactId;
    }

    /**
     * Getter de l'attribut cksum.
     * 
     * @return Returns L'attribut cksum.
     */
    public int getCksum() {
        return cksum;
    }

    /**
     * Méthode getDescription.
     * 
     * @return String
     */
    public String getDescription() {
        return description;
    }

    /**
     * Méthode getDestination.
     * 
     * @return String
     */
    public String getDestinations() {
        final StringBuffer sb = new StringBuffer();
        if (destinations != null) {
            int i = 0;
            for (final String destination : destinations) {
                if (i++ > 0) {
                    sb.append(", ");
                }
                sb.append(destination);
            }
        }
        return sb.toString();
    }

    /**
     * Méthode getDestinations.
     * 
     * @return Collection<String>
     */
    public Collection<String> getDestinationsList() {
        return destinations;
    }

    /**
     * Getter de l'attribut groupId.
     * 
     * @return Returns L'attribut groupId.
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * Retourne le "base name" d'un fichier jar sans la version. Exemple :
     * commons-net-1.4.1.jar ==> commons-net
     * 
     * @return Le nom du jar sans l'extension et sans la version
     */
    public String getJarBaseName() {
        final String baseJarName = getJarName();
        if (MODE_BASENAME.equalsIgnoreCase(mode)) {
            String jarName = baseJarName.replaceAll("-SNAPSHOT", "");
            jarName = jarName.replaceAll(".jar", "");
            final StringTokenizer st = new StringTokenizer(jarName, "-");
            if (st.countTokens() > 0) {
                final StringBuffer baseName = new StringBuffer();
                String token = "";

                for (int i = 0; i < st.countTokens(); i++) {
                    token = st.nextToken();
                    if (i > 0) {
                        baseName.append("-");
                    }
                    baseName.append(token);
                }

                // Cas particulier pour les jars sans version (ex. libx-run.jar,
                // il faut quand même ajouter "run")
                if (st.hasMoreTokens()) {
                    token = st.nextToken();
                    if (isAlpha(token)) {
                        baseName.append("-").append(token);
                    }
                }

                return baseName.toString();
            } else {
                return jarName;
            }
        } else {
            return baseJarName;
        }
    }

    /**
     * Getter de l'attribut jarName.
     * 
     * @return Returns L'attribut jarName.
     */
    public String getJarName() {
        if ((jarName == "") && (artifactId != null) && (version != null)) {
            jarName = artifactId + "-" + version + ".jar";
        }
        return jarName;
    }

    /**
     * Méthode getKey.
     * 
     * @return String
     */
    public String getKey() {
        return key;
    }

    /**
     * Méthode getModules.
     * 
     * @return String
     */
    public String getModules() {
        final StringBuffer sb = new StringBuffer();
        if (modules != null) {
            int i = 0;
            for (final String module : modules) {
                if (i++ > 0) {
                    sb.append(", ");
                }
                sb.append(module);
            }
        }
        return sb.toString();
    }

    /**
     * Méthode getModules.
     * 
     * @return Collection<String>
     */
    public Collection<String> getModulesList() {
        return modules;
    }

    /**
     * Getter de l'attribut origin.
     * 
     * @return Returns L'attribut origin.
     */
    public Collection<String> getOrigins() {
        return origins;
    }

    /**
     * Méthode getPath.
     * 
     * @return String
     */
    public String getPath() {
        return path;
    }

    /**
     * Getter de l'attribut startConstantsKey.
     * 
     * @return Returns L'attribut startConstantsKey.
     */
    public String getStartConstantsKey() {
        return startConstantsKey;
    }

    /**
     * Getter de l'attribut startConstantsRegExp.
     * 
     * @return Returns L'attribut startConstantsRegExp.
     */
    public String getStartConstantsRegExp() {
        return startConstantsRegExp;
    }

    /**
     * Getter de l'attribut version.
     * 
     * @return Returns L'attribut version.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Méthode hasOrigin.
     * 
     * @param clazz
     *            the clazz
     * @return boolean
     */
    public boolean hasOrigin(final Class<?> clazz) {
        final String className = buildOriginClassName(clazz);
        for (final String origin : origins) {
            if (origin.equalsIgnoreCase(className)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Méthode isActif.
     * 
     * @return boolean
     */
    public boolean isActif() {
        return actif;
    }

    /**
     * Méthode setActif.
     * 
     * @param actif
     *            void
     */
    public void setActif(final boolean actif) {
        this.actif = actif;
    }

    /**
     * Méthode setActif.
     * 
     * @param actif
     *            void
     */
    public void setActif(final String actif) {
        this.actif = Boolean.parseBoolean(actif);
    }

    /**
     * Setter de l'attribut artifactId.
     * 
     * @param artifactId
     *            L'attribut artifactId.
     */
    public void setArtifactId(final String artifactId) {
        this.artifactId = artifactId;
    }

    /**
     * Setter de l'attribut cksum.
     * 
     * @param cksum
     *            L'attribut cksum.
     */
    public void setCksum(final int cksum) {
        this.cksum = cksum;
    }

    /**
     * Méthode setDescription.
     * 
     * @param description
     *            the new description
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Méthode setDestinations.
     * 
     * @param destinations
     *            void
     */
    public void setDestinations(final Collection<String> destinations) {
        this.destinations = destinations;
    }

    /**
     * Méthode setDestination.
     * 
     * @param destination
     *            the new destinations
     */
    public void setDestinations(final String destination) {
        if (!StringUtils.isEmpty(destination)) {
            final String[] dests = destination.split(",");
            destinations = new TreeSet<String>();
            for (final String dest : dests) {
                destinations.add(dest.trim());
            }
        }
    }

    /**
     * Setter de l'attribut groupId.
     * 
     * @param groupId
     *            L'attribut groupId.
     */
    public void setGroupId(final String groupId) {
        this.groupId = groupId;
    }

    /**
     * Setter de l'attribut jarName.
     * 
     * @param jarName
     *            L'attribut jarName.
     */
    public void setJarName(final String jarName) {
        this.jarName = jarName;
        setVersion(extractVersion(jarName));
        setArtifactId(extractArtifactId(jarName));
        // setGroupId(extractGroupId(jarName));
    }

    /**
     * Méthode setKey.
     * 
     * @param key
     *            void
     */
    public void setKey(final String key) {
        this.key = key;
    }

    /**
     * Méthode setModules.
     * 
     * @param modules
     *            void
     */
    public void setModules(final Collection<String> modules) {
        this.modules = modules;
    }

    /**
     * Méthode setDestination.
     * 
     * @param modules
     *            the new modules
     */
    public void setModules(final String modules) {
        if (!StringUtils.isEmpty(modules)) {
            final String[] mods = modules.split(",");
            this.modules = new TreeSet<String>();
            for (final String mod : mods) {
                this.modules.add(mod.trim());
            }
        }
    }

    /**
     * Setter de l'attribut origin.
     * 
     * @param origin
     *            L'attribut origin.
     */
    public void setOrigin(final Collection<String> origin) {
        origins = origin;
    }

    /**
     * Méthode setPath.
     * 
     * @param path
     *            void
     */
    public void setPath(final String path) {
        this.path = path;
    }

    /**
     * Setter de l'attribut startConstantsKey.
     * 
     * @param startConstantsKey
     *            L'attribut startConstantsKey.
     */
    public void setStartConstantsKey(final String startConstantsKey) {
        this.startConstantsKey = startConstantsKey;
    }

    /**
     * Setter de l'attribut startConstantsRegExp.
     * 
     * @param startConstantsRegExp
     *            L'attribut startConstantsRegExp.
     */
    public void setStartConstantsRegExp(final String startConstantsRegExp) {
        this.startConstantsRegExp = startConstantsRegExp;
    }

    /**
     * Setter de l'attribut version.
     * 
     * @param version
     *            L'attribut version.
     */
    public void setVersion(final String version) {
        this.version = version;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    /**
     * To string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        int i = 0;
        for (final String className : origins) {
            if (i++ > 0) {
                sb.append(", ");
            }
            sb.append(className);
        }
        return getJarName() + " [" + sb.toString() + "]";
    }

}
