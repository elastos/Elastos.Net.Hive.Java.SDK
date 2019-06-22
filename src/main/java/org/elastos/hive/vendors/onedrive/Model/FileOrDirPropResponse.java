package org.elastos.hive.vendors.onedrive.Model;

/**
 * Package: org.elastos.hive.vendors.onedrive.Model
 * ClassName: FileOrDirPropResponse
 * Created by ranwang on 2019/6/21.
 */
/*
{
	"lastModifiedDateTime": "2019-06-21T07:34:15.74Z",
	"lastModifiedBy": {
		"application": {
			"displayName": "HiveTest",
			"id": "4827420a"
		},
		"user": {
			"displayName": "",
			"id": "663768767e19dfc2"
		}
	},
	"createdDateTime": "2019-06-21T07:34:15.43Z",
	"@odata.context": "https://graph.microsoft.com/v1.0/$metadata#users('15011297029%40163.com')/drive/root/$entity",
	"parentReference": {
		"path": "/drive/root:",
		"driveId": "663768767e19dfc2",
		"driveType": "personal",
		"id": "663768767E19DFC2!101"
	},
	"folder": {
		"view": {
			"sortOrder": "ascending",
			"viewType": "thumbnails",
			"sortBy": "name"
		},
		"childCount": 1
	},
	"size": 0,
	"createdBy": {
		"application": {
			"displayName": "HiveTest",
			"id": "4827420a"
		},
		"user": {
			"displayName": "",
			"id": "663768767e19dfc2"
		}
	},
	"webUrl": "https://1drv.ms/f/s!AMLfGX52aDdmag",
	"name": "root",
	"cTag": "adDo2NjM3Njg3NjdFMTlERkMyITEwNi42MzY5NjY5OTI1NTc0MDAwMDA",
	"eTag": "aNjYzNzY4NzY3RTE5REZDMiExMDYuMA",
	"id": "663768767E19DFC2!106",
	"fileSystemInfo": {
		"lastModifiedDateTime": "2019-06-21T07:34:15.43Z",
		"createdDateTime": "2019-06-21T07:34:15.43Z"
	}
}
 */
public class FileOrDirPropResponse {
    private String id ;
    private Folder folder ;

    public FileOrDirPropResponse(String id) {
        this.id = id;
    }

    public FileOrDirPropResponse(String id, Folder folder) {
        this.id = id;
        this.folder = folder;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    public class Folder {
        private View view ;
        private int childCount ;

        public Folder(View view, int childCount) {
            this.view = view;
            this.childCount = childCount;
        }

        public View getView() {
            return view;
        }

        public void setView(View view) {
            this.view = view;
        }

        public int getChildCount() {
            return childCount;
        }

        public void setChildCount(int childCount) {
            this.childCount = childCount;
        }

        public class View {
            String sortOrder ;
            String viewType ;
            String sortBy ;

            public View(String sortOrder, String viewType, String sortBy) {
                this.sortOrder = sortOrder;
                this.viewType = viewType;
                this.sortBy = sortBy;
            }

            public String getSortOrder() {
                return sortOrder;
            }

            public void setSortOrder(String sortOrder) {
                this.sortOrder = sortOrder;
            }

            public String getViewType() {
                return viewType;
            }

            public void setViewType(String viewType) {
                this.viewType = viewType;
            }

            public String getSortBy() {
                return sortBy;
            }

            public void setSortBy(String sortBy) {
                this.sortBy = sortBy;
            }
        }

    }
}
