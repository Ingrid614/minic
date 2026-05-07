#!/bin/bash

# =======================
# Compilation du projet
# =======================

echo "======================="
echo " COMPILATION"
echo "======================="

ant -f minic-build.xml generate || exit 1
ant -f minic-build.xml compile || exit 1

# =======================
# Dossiers
# =======================

mkdir -p tests/tam
mkdir -p tests/results
mkdir -p tests/errors

# Nettoyage anciens fichiers
rm -f tests/tam/*.tam
rm -f tests/results/*.log
rm -f tests/errors/*.error

# =======================
# Classpath
# =======================

CP="bin/cls:tools/commons-lang3-3.7.jar:tools/commons-text-1.2.jar:tools/antlr-4.13.1-complete.jar:$CLASSPATH"

# =======================
# TESTS VALIDES
# =======================

echo ""
echo "======================="
echo " TESTS VALIDES"
echo "======================="

for file in tests/valide/*.txt
do
    [ -e "$file" ] || continue

    filename=$(basename "$file" .txt)

    echo ""
    echo "Compilation de $filename"

    # Exécution
    java -cp "$CP" fr.n7.stl.minic.Driver "$file" \
        > tests/results/"$filename".log \
        2>&1

    # TAM généré
    generated_tam="tests/valide/$filename.tam"

    if [ -f "$generated_tam" ]; then
        mv "$generated_tam" tests/tam/
        echo " OK -> tests/tam/$filename.tam"
    else
        echo " ECHEC : pas de fichier TAM généré"
        echo " Voir tests/results/$filename.log"
    fi
done

# =======================
# TESTS INVALIDES
# =======================

echo ""
echo "======================="
echo " TESTS INVALIDES"
echo "======================="

for file in tests/invalide/*.txt
do
    [ -e "$file" ] || continue

    filename=$(basename "$file" .txt)

    echo ""
    echo "Test invalide $filename"

    java -cp "$CP" fr.n7.stl.minic.Driver "$file" \
        > tests/errors/"$filename".error \
        2>&1

    echo " Erreurs -> tests/errors/$filename.error"
done

# =======================
# FIN
# =======================

echo ""
echo "======================="
echo " FIN TESTS"
echo "======================="