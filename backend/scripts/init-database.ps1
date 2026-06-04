$ErrorActionPreference = "Stop"

$backendRoot = Split-Path -Parent $PSScriptRoot
$schemaFile = Join-Path $backendRoot "sql\01_create_tables.sql"
$seedFile = Join-Path $backendRoot "sql\02_seed_data.sql"

$mysqlCommand = "mysql"

if (-not (Get-Command $mysqlCommand -ErrorAction SilentlyContinue)) {
  throw "mysql command was not found. Please add MySQL bin directory to PATH and retry."
}

function Invoke-MysqlFile {
  param(
    [Parameter(Mandatory = $true)]
    [string] $SqlFile
  )

  $mysqlPath = (Get-Command $mysqlCommand).Source
  $command = "`"$mysqlPath`" -uroot -proot --default-character-set=utf8mb4 --binary-mode < `"$SqlFile`""

  cmd.exe /c $command

  if ($LASTEXITCODE -ne 0) {
    throw "Failed to execute SQL file: $SqlFile"
  }
}

Write-Host "Creating database and tables from $schemaFile"
Invoke-MysqlFile -SqlFile $schemaFile

Write-Host "Seeding initial data from $seedFile"
Invoke-MysqlFile -SqlFile $seedFile

Write-Host "Database second_hand_trade is ready."
