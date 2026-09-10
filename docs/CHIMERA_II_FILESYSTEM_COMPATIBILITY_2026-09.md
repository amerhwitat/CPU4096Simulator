# Chimera II Filesystem Compatibility Simulation

The simulator's storage model must expose filesystem semantics independently from the physical disk image. The UI should classify each format by capability and compatibility layer.

Supported capability groups include ext4/XFS/Btrfs/ZFS/JFS/JFS2/UFS/System-V, FAT/NTFS, ISO/UDF, Amiga OFS/FFS/CrossDOS, HP-UX VxFS, OpenVMS ODS-2/ODS-5, HFS/HFS+, QNX, ADFS, AFS, 9P, NFS/SMB, cluster/distributed formats, pseudo-filesystems and flash filesystems.

ODS-5 should expose long names, case preservation and deeper directory semantics; ODS-2 remains the restricted Files-11 compatibility profile. AmigaDOS should expose handler-style packet semantics and DOS0/DOS1/DOS2/DOS3 volume identification.
