#r "System.IO.Compression"

using System;
using System.IO;
using System.IO.Compression;
using System.Linq;

string projectRoot = Environment.CurrentDirectory;

string[] itemsToZip = File.ReadAllLines(Path.Combine(projectRoot, "paths.txt"));

string outputDirectory = projectRoot;
string releaseFileName = "CPG.zip";
string zipRootFolder = "CPG";
string destinationZip = Path.Combine(outputDirectory, releaseFileName);

try
{
    if (File.Exists(destinationZip))
        File.Delete(destinationZip);

    using MemoryStream memoryStream = new MemoryStream();
    using ZipArchive archive = new ZipArchive(memoryStream, ZipArchiveMode.Create, true);
    foreach (string item in itemsToZip)
    {
        string sourcePath = Path.Combine(projectRoot, item);

        if (Directory.Exists(sourcePath))
        {
            Console.ForegroundColor = ConsoleColor.Green;
            Console.WriteLine($"Writing {item}...");
            Console.ResetColor();

            string[] files = Directory.GetFiles(sourcePath, "*", SearchOption.AllDirectories);

            foreach (string file in files)
            {
                string relativePath = Path.GetRelativePath(projectRoot, file).Replace('\\', '/');

                string entryPath = $"{zipRootFolder}/{relativePath}";

                var zipEntry = archive.CreateEntry(entryPath, CompressionLevel.Optimal);

                using Stream entryStream = zipEntry.Open();
                using FileStream fileStream = File.OpenRead(file);
                fileStream.CopyTo(entryStream);
            }
        }
        else
        {
            if (File.Exists(item))
            {
                Console.ForegroundColor = ConsoleColor.Green;
                Console.WriteLine($"Writing {item}...");
                Console.ResetColor();

                string relativePath = Path.GetRelativePath(projectRoot, item).Replace('\\', '/');

                string entryPath = $"{zipRootFolder}/{relativePath}";

                var zipEntry = archive.CreateEntry(entryPath, CompressionLevel.Optimal);

                using Stream entryStream = zipEntry.Open();
                using FileStream fileStream = File.OpenRead(item);
                fileStream.CopyTo(entryStream);
            }
            else
            {
                Console.ForegroundColor = ConsoleColor.Red;
                Console.WriteLine($"{item} not found.");
                Console.ResetColor();
            }
        }
    }
    archive.Dispose();
    byte[] zipBytes = memoryStream.ToArray();
    memoryStream.Close();
    memoryStream.Dispose();

    if (zipBytes.Length > 0)
    {
        Console.ForegroundColor = ConsoleColor.Cyan;
        Console.WriteLine("Writing compiled memory buffer out to disk...");
        Console.ResetColor();

        File.WriteAllBytes(destinationZip, zipBytes.ToArray());

        Console.ForegroundColor = ConsoleColor.Green;
        Console.WriteLine($"Generated at: {destinationZip}");
        Console.ResetColor();
    }
    else
    {
        Console.ForegroundColor = ConsoleColor.Red;
        Console.WriteLine("Build failed: No files were streamed into the memory matrix.");
        Console.ResetColor();
    }
}
catch (Exception ex)
{
    Console.ForegroundColor = ConsoleColor.Red;
    Console.WriteLine($"An error occurred during packing: {ex.Message}");
    Console.ResetColor();
}