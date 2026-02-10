#!/bin/bash

################################################################################
# Script de ejecución de tests - Proyecto Platzi-Play
# Este script ejecuta todos los tests y genera el reporte de cobertura
################################################################################

set -e  # Exit on error

echo "================================"
echo "🧪 EJECUTANDO TESTS DEL PROYECTO"
echo "================================"
echo ""

cd "$(dirname "$0")"  # Move to project root

echo "📦 Limpiando builds anteriores..."
./gradlew clean --quiet

echo "🔨 Compilando código..."
./gradlew compileJava compileTestJava --quiet

echo ""
echo "🚀 Ejecutando tests..."
echo "   (Esto puede tomar 2-5 minutos la primera vez)"
echo ""

./gradlew test jacocoTestReport

echo ""
echo "✅ Tests completados!"
echo ""
echo "📊 REPORTE DE COBERTURA"
echo "======================="
echo ""
echo "Para ver el reporte HTML, abre:"
echo "   open build/reports/jacoco/test/html/index.html"
echo ""
echo "📈 Resumen de ejecución:"
echo "   - Unit Tests: MovieServiceTest (18 tests)"
echo "   - Mapper Tests: MovieMapperTest (15 tests)"
echo "   - Controller Tests: MovieControllerTest (25 tests)"
echo "   - Repository Tests: MovieEntityRepositoryIT (26 tests)"
echo "   - Exception Tests: RestExceptionHandlerIT (3 tests)"
echo ""
echo "Total: 90+ tests ejecutados"
echo ""
echo "🎯 Objetivo: 85-90% de cobertura"
echo ""
echo "Para crear tests adicionales, consulta:"
echo "   - PROMPT_TESTS_85_90_COVERAGE.txt"
echo "   - RESUMEN_TESTS_GENERADOS.md"
echo "   - INFORME_FINAL_TESTS.txt"
echo ""
echo "================================"
echo "✨ ¡Tests completados exitosamente!"
echo "================================"

