#!/bin/bash
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Caminho dos jars para a máquina da faculdade (Newton)
CP="bin/cls:tools/commons-lang3-3.7.jar:tools/commons-text-1.2.jar:tools/antlr-4.13.1-complete.jar"

echo -e "${YELLOW}=== Running All Positive Tests ===${NC}"

# Procura por todos os arquivos .txt na pasta de positivos
for test in tests/positives/*.txt; do
    if [ -f "$test" ]; then
        echo -n "Testing $(basename "$test"): "
        
        # Executa o Driver
        java -cp "$CP" fr.n7.stl.minic.Driver "$test" > /dev/null 2>&1
        
        if [ $? -eq 0 ]; then
            echo -e "${GREEN}SUCCESS${NC}"
            # Renomeia o input.tam gerado para o nome do teste original
            # Ex: tests/positives/test_records.txt -> tests/positives/test_records.tam
            mv input.tam "${test%.txt}.tam" 2>/dev/null
        else
            echo -e "${RED}FAILED${NC}"
        fi
    fi
done

echo
echo -e "${YELLOW}=== Running All Negative Tests ===${NC}"

# Procura por todos os arquivos .txt na pasta de negativos
for test in tests/negatives/*.txt; do
    if [ -f "$test" ]; then
        echo -n "Testing $(basename "$test"): "
        
        java -cp "$CP" fr.n7.stl.minic.Driver "$test" > /dev/null 2>&1
        
        # Para negativos, o sucesso é o compilador REJEITAR (retorno != 0)
        if [ $? -ne 0 ]; then
            echo -e "${GREEN}OK (Error Detected)${NC}"
        else
            echo -e "${RED}FAILED (Error NOT Detected)${NC}"
        fi
    fi
done

echo
echo -e "${YELLOW}=== Testing Finished ===${NC}"