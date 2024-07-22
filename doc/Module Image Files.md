# Module Image Files

Module image files are optimized for selective loading of the content. This means that function code may be loaded when
needed for execution or in the background which might be important for startup time optimization.

While loading modules it's sufficient to just know where to look for functions so only the module descriptor can be
loaded at first.

Structure:

* version - 1 byte - always 1
* module descriptor - variable size - contains module name, package names and function names (types?) with offset range
  from the beginning of the file where the function is located
* series of compiled functions body code

## Module Descriptor

* module name - UTF String
* package count - 2 bytes (short)
* repeated for every package
  * package name - UTF String
  * function count - 2 bytes (short)
  * repeated for every function
    * function name - UTF String
    * compiled function body - variable size
  
## Saving the file

Generating the file is nontrivial since module descriptor has to have the offset of the functions, but they are
dependent on the module descriptor size itself.

The process is done in multiple phases:

* File version is written
* Module descriptor is generated with offsets set to 0 and written to file.
* Then the function body serialization starts. We can use the `DataOutputStream.size()` to generate and update the
  offsets of the function bodies (in-memory for the time being)
* Module descriptor is generated with correct offsets and written the second time.
