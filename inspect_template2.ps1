Add-Type -AssemblyName System.IO.Compression.FileSystem
$path = 'c:\Users\Lenovo\Desktop\DossierExpert-houda\src\main\resources\templates\Dossiertemplate.docx'
if (-not (Test-Path $path)) { Write-Host "MISSING: $path"; exit 1 }
$zip = [System.IO.Compression.ZipFile]::OpenRead($path)
$entry = $zip.Entries | Where-Object { $_.FullName -eq 'word/document.xml' }
if ($entry -eq $null) { Write-Host 'missing document.xml'; exit 1 }
$sr = $entry.Open()
$reader = New-Object System.IO.StreamReader($sr)
$content = $reader.ReadToEnd()
$reader.Close(); $sr.Close()
$zip.Dispose()
$pattern = 'date_creation'
$idx = $content.IndexOf($pattern)
Write-Host "index:" $idx
if ($idx -ge 0) {
    $start = [Math]::Max(0, $idx-200)
    $len = [Math]::Min(500, $content.Length - $start)
    Write-Host $content.Substring($start, $len)
} else {
    Write-Host 'pattern not found'
}
$pattern2 = '{'
$matches = [regex]::Matches($content, '\{[^\}]+\}')
Write-Host 'MATCHES:' ($matches.Count)
foreach ($m in $matches) { Write-Host $m.Value }
