### SERVIDOR DE IMPRESIÓN CON JAVA

** Ejecutarlo como servicio de windows: sc create PrinterServer binPath= "C:\Users\Admin\Documents\Spring Web\printerServer.exe" start= auto DisplayName= "PrinterServer"
** Si el primero no funciona usar este: sc create PrinterServer start= auto binPath= "C:\cloudflared\printerServer.exe" DisplayName= PrinterServer
** Ver detalles del servicio: sc qc PrinterServer
** Ver el estado del servicio: 