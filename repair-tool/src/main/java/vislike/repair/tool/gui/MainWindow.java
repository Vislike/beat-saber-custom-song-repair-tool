package vislike.repair.tool.gui;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vislike.repair.tool.repair.RepairFileEntry;
import vislike.repair.tool.repair.result.FileStatus;
import vislike.repair.tool.repair.result.RepairResult;
import vislike.repair.tool.utils.Resources;
import vislike.repair.tool.utils.Settings;

public class MainWindow implements AutoCloseable {

	private static final Logger logger = LoggerFactory.getLogger(MainWindow.class);

	private Display display = null;
	private Image icon = null;
	private Shell shell = null;
	private Text statusText = null;

	public MainWindow() {
		display = new Display();
		shell = new Shell(display);
		shell.setLayout(new GridLayout(1, false));
		shell.setSize(700, 350);
		shell.setText("Beat Saber Custom Song Repair Tool v" + Resources.getVersionInfo());
		icon = Resources.getIcon(display);
		shell.setImage(icon);

		// File group
		Group fileGroup = new Group(shell, SWT.NONE);
		fileGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fileGroup.setLayout(new GridLayout(2, false));
		fileGroup.setText("Song File");

		Text fileInput = new Text(fileGroup, SWT.SINGLE | SWT.BORDER);
		fileInput.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fileInput.setText(Settings.getInstance().getInputFile());

		FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
		fileDialog.setFilterExtensions(new String[] { "info.dat" });
		fileDialog.setFilterNames(new String[] { "Custom Song" });

		Button browse = new Button(fileGroup, SWT.PUSH);
		browse.setText("Browse");

		// Status text
		statusText = new Text(shell, SWT.MULTI | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		statusText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		statusText.setText(Resources.getInfoTxt());

		// Repair button
		Button repair = new Button(shell, SWT.PUSH);
		repair.setText("Repair");
		repair.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

		// Actions
		fileInput.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				statusText.setText("File path manualy altered, choose repair to attempt to repair the file.");
			}
		});

		browse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Restore last directory
				fileDialog.setFilterPath(Settings.getInstance().getBrowseDirectory());

				// Open dialog
				String file = fileDialog.open();
				if (file != null) {
					fileInput.setText(file);
					statusText.setText("File selected from dialog, choose repair to attempt to repair the file.");
					Settings.getInstance().saveInputFile(file);
					Settings.getInstance().saveBrowseDirectory(fileDialog.getFilterPath());
				}
			}
		});

		repair.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Settings.getInstance().saveInputFile(fileInput.getText());
				RepairResult status = RepairFileEntry.repairFile(fileInput.getText());

				switch (status.code()) {
				case NOTHING:
					statusText.setText("No problem found.");
					break;
				case SUCCESS:
					StringBuilder sb = new StringBuilder();
					sb.append("Success:\n");
					for (FileStatus fs : status.fileStatus()) {
						sb.append("Applied [");
						sb.append(fs.status());
						sb.append("] ");
						sb.append(fs.file());
						sb.append("\n");
					}
					statusText.setText(sb.toString());
					break;
				case FAILED:
				default:
					statusText.setText("A problem occured, message:\n" + status.message());
					break;
				}
			}
		});
	}

	public void run() throws IOException {
		shell.open();
		logger.info("Repair Tool Started");

		while (!shell.isDisposed()) {
			try {
				if (!display.readAndDispatch())
					display.sleep();
			} catch (Exception e) {
				logger.error("Unexpected problem occurred", e);
				try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
					e.printStackTrace(pw);
					statusText.setText("Unexpected problem occurred:\n" + sw.toString());
				}
			}
		}
	}

	@Override
	public void close() throws Exception {
		if (icon != null) {
			icon.dispose();
		}
		if (display != null) {
			display.dispose();
		}
	}
}
