# BonApiPdf

Este proyecto pretende ofrecer un servicio REST que dada una estructura Json y una plantilla rellene un doc PDF.
Más adelante se incluirá un conector bonita para poder ser fácilmente utilizado desde la versión community de Bonita.

La versión actual solo ofrece la posibilidad de generar un PDF a partir de un esquema fijo por programa.

Esta API está basada en la libreria de <b>iText</b>:<br />
https://itextpdf.com/es
<br />
a quienes agradecemos su excelente labor.

| Master      | Docs        | License|
|-------------|-------------|--------|
|master|doc|<a href="./LICENSE"><img src="./imgs/AGPLv3_Logo.svg" width="100" /></a>|


## M�todos incluidos en esta API
<b>version</b>
	Retorna la versi�n actual de la API
<b>pdfDemo</b>
	Retorna un PDF de ejemplo
<b>getPDF</b>
	Dado un json con la estrucutura predeterminada devuelve un PDF con una plantilla fija de momento
<b>savePDF</b>
	Dado un json con la estrucutura predeterminada guarda un PDF en el servidor creado con una plantilla fija de momento y retorna la url desde donde acceder a dicho PDF
	 
	
