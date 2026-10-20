# Summary
- Permitir que el usuario revise y confirme una transferencia antes de enviarla, mostrando un resumen y validando los datos.
- Impacto esperado: reducir las quejas por transferencias duplicadas en un 20% durante un mes.

# Scope
- **Dentro del alcance**
  - Confirmación previa al envío.
  - Mensaje resumen.
  - Validación de datos.
- **Fuera del alcance**
  - Cambios de infraestructura.
  - Rediseño completo de la interfaz.

# Supuestos
- La funcionalidad se integrará con la API REST, el frontend web y los servicios existentes de cuentas y transferencias.
- Los campos del resumen y las reglas concretas de validación aún no están definidos.

# Decisiones técnicas por tomar (pendientes o resueltas)
- **Resuelto:** usar la API REST, el frontend web y el logging estandarizado existentes.
- **Pendiente:** acordar campos del resumen y reglas de validación, incluido el monto.
- **Pendiente:** acordar línea base y método para medir las quejas por duplicados.

# Riesgos y mitigaciones
- Exposición de datos sensibles en logs: revisar los eventos y excluir datos sensibles.
- Errores en la validación de montos: acordar reglas y cubrir casos válidos e inválidos con pruebas.
- Dependencia de los servicios de cuentas y transferencias: verificar sus contratos y probar respuestas de error.

# Criterios de aceptación (medibles)
- El 100% de las transferencias iniciadas por el nuevo flujo muestra una confirmación antes del envío.
- El 100% de los intentos sin confirmación queda sin enviar.
- El resumen se muestra antes del envío en el 100% de los intentos que alcanzan la confirmación.
- En el primer mes de medición, las quejas por transferencias duplicadas disminuyen al menos un 20% frente a la línea base acordada.

# Plan de validación
- **Pruebas unitarias**
  - Verificar el envío tras confirmar y que cancelar o no confirmar no envíe la transferencia.
  - Cubrir las reglas de validación acordadas, incluidos los errores de monto.
  - Verificar que el resumen presente los datos acordados.
- **Pruebas funcionales**
  - Recorrer el flujo de resumen, confirmación y respuesta de los servicios.
  - Verificar el manejo de errores de cuentas y transferencias.
- **Seguridad (comprobaciones mínimas)**
  - Revisar que los logs no expongan datos sensibles.
  - Comprobar que los datos inválidos no se envíen.
- **Evidencia requerida (qué adjuntar)**
  - Salida de pruebas unitarias y funcionales.
  - Checklist de revisión de logs y validaciones.
  - Documentación breve actualizada y PR revisado.

# Tareas sugeridas por rol
- **Chapter/PO**
  - Confirmar los campos del resumen.
  - Definir las reglas de validación, incluido el monto.
  - Acordar la línea base y la fuente de medición de quejas.
- **Tech Lead**
  - Revisar los contratos de los servicios de cuentas y transferencias.
  - Definir el comportamiento esperado ante errores de dependencias.
  - Revisar el diseño de logging para evitar exposición de datos.
- **Backend**
  - Implementar las validaciones acordadas.
  - Garantizar que el envío ocurra solo tras la confirmación.
  - Añadir pruebas unitarias para validaciones y respuestas de servicios.
- **Frontend**
  - Mostrar el resumen antes del envío.
  - Implementar la confirmación y evitar envíos sin ella.
  - Presentar errores de validación y de servicios.
- **QA**
  - Diseñar casos para confirmar, cancelar y enviar datos inválidos.
  - Verificar el flujo con respuestas exitosas y fallidas de dependencias.
  - Adjuntar resultados de pruebas y checklist de evidencia.
- **Cyber**
  - Revisar que los logs no incluyan datos sensibles.
  - Comprobar que los datos inválidos no se envíen.
  - Revisar hallazgos de seguridad del PR.

# Preguntas abiertas
- ¿Qué datos debe incluir el resumen?
- ¿Cuáles son las reglas exactas para validar el monto y los demás datos?
- ¿Cuál será la línea base y la fuente para medir las quejas por duplicados?
- ¿Qué comportamiento se espera si falla el servicio de cuentas o el de transferencias?
