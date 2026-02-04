#!/bin/bash

# Script para cambiar entre modo LOCAL y GMAIL REAL
# Uso: ./cambiar-modo-email.sh [local|gmail]

PROPERTIES_FILE="src/main/resources/application.properties"

if [ "$1" == "local" ]; then
    echo "🔄 Cambiando a modo LOCAL (desarrollo)..."

    # Comentar configuración Gmail
    sed -i 's/^spring.mail.host=smtp.gmail.com/#spring.mail.host=smtp.gmail.com/' $PROPERTIES_FILE
    sed -i 's/^spring.mail.port=587/#spring.mail.port=587/' $PROPERTIES_FILE
    sed -i 's/^spring.mail.username=.*@gmail.com/#spring.mail.username=TU_CORREO@gmail.com/' $PROPERTIES_FILE
    sed -i 's/^spring.mail.password=.*/#spring.mail.password=tu-contraseña-app/' $PROPERTIES_FILE
    sed -i 's/^greenmail.enabled=false/#greenmail.enabled=false/' $PROPERTIES_FILE

    # Descomentar configuración local
    sed -i 's/^#spring.mail.host=localhost/spring.mail.host=localhost/' $PROPERTIES_FILE
    sed -i 's/^#spring.mail.port=3025/spring.mail.port=3025/' $PROPERTIES_FILE
    sed -i 's/^#greenmail.enabled=true/greenmail.enabled=true/' $PROPERTIES_FILE

    echo "✅ Modo LOCAL activado"
    echo "📬 Los correos se mostrarán en consola"

elif [ "$1" == "gmail" ]; then
    echo "🔄 Cambiando a modo GMAIL REAL..."

    # Comentar configuración local
    sed -i 's/^spring.mail.host=localhost/#spring.mail.host=localhost/' $PROPERTIES_FILE
    sed -i 's/^spring.mail.port=3025/#spring.mail.port=3025/' $PROPERTIES_FILE
    sed -i 's/^greenmail.enabled=true/#greenmail.enabled=true/' $PROPERTIES_FILE

    # Descomentar configuración Gmail
    sed -i 's/^#spring.mail.host=smtp.gmail.com/spring.mail.host=smtp.gmail.com/' $PROPERTIES_FILE
    sed -i 's/^#spring.mail.port=587/spring.mail.port=587/' $PROPERTIES_FILE
    sed -i 's/^#greenmail.enabled=false/greenmail.enabled=false/' $PROPERTIES_FILE

    echo "✅ Modo GMAIL activado"
    echo "⚠️  RECUERDA: Configura tu email y contraseña de aplicación en application.properties"
    echo "📧 Los correos llegarán realmente al destinatario"

else
    echo "❌ Uso: ./cambiar-modo-email.sh [local|gmail]"
    echo ""
    echo "Ejemplos:"
    echo "  ./cambiar-modo-email.sh local   - Cambiar a servidor local (desarrollo)"
    echo "  ./cambiar-modo-email.sh gmail   - Cambiar a Gmail real (producción)"
    exit 1
fi

echo ""
echo "🔄 Reinicia la aplicación para aplicar cambios"

