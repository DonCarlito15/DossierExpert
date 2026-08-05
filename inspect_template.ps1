Add-Type -AssemblyName System.IO.Compression.FileSystem
$path = 'c:\Users\Lenovo\Desktop\DossierExpert-houda\src\main\resources\templates\Dossiertemplate.docx'
if (-not (Test-Path $path)) {
    Write-Host "MISSING: $path"
    exit 1
}
$zip = [System.IO.Compression.ZipFile]::OpenRead($path)
$files = @('word/document.xml','word/header1.xml','word/footer1.xml')
foreach ($f in $files) {
    Write-Host "FILE: $f"
    $entry = $zip.Entries | Where-Object { $_.FullName -eq $f }
    if ($entry -eq $null) { Write-Host '  missing'; continue }
    $sr = $entry.Open()
    $reader = New-Object System.IO.StreamReader($sr)
    $content = $reader.ReadToEnd()
    $reader.Close()
    $sr.Close()
    $matches = [regex]::Matches($content, '\{[^\}]+\}')
    foreach ($m in $matches) { Write-Host "  $($m.Value)" }
}
$zip.Dispose()
