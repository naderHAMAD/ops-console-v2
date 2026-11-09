import os, zipfile

VERSIONS = ["7.4.0-alpha1", "7.4.0-alpha2", "7.4.0-alpha3", "7.4.0", "7.4.1", "7.5.0"]
OUT = os.environ.get("ARTIFACT_DIR", "/srv")
os.makedirs(OUT, exist_ok=True)

for v in VERSIONS:
    root = "jboss-eap-%s" % v
    path = os.path.join(OUT, "%s.zip" % root)
    with zipfile.ZipFile(path, "w", zipfile.ZIP_DEFLATED) as z:
        z.writestr("%s/version.txt" % root, v)
        z.writestr("%s/bin/standalone.sh" % root, "#!/bin/sh\necho fake JBoss EAP %s\n" % v)
        z.writestr("%s/README.txt" % root, "Livraison de test JBoss EAP %s\n" % v)
    print("cree :", path)
