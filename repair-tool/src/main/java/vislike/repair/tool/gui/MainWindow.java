package vislike.repair.tool.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vislike.repair.tool.repair.RepairFileEntry;
import vislike.repair.tool.repair.RepairType;
import vislike.repair.tool.repair.result.RepairResult;
import vislike.repair.tool.utils.Resources;
import vislike.repair.tool.utils.Settings;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class MainWindow implements AutoCloseable {

	private static final Logger logger = LoggerFactory.getLogger(MainWindow.class);

	final private Display display;
	final private Image icon;
	final private Shell shell;
	final private Text statusText;
	private Dialog dialog;

	public MainWindow() {
		display = new Display();
		shell = new Shell(display);
		shell.setLayout(new GridLayout(1, false));
		shell.setSize(700, 350);
		shell.setText("Beat Saber Custom Song Repair Tool v" + Resources.getVersionInfo());
		icon = Resources.getIcon(display);
		shell.setImage(icon);

		Button[] repairType = new Button[2];

		repairType[0] = new Button(shell, SWT.RADIO);
		repairType[0].setText("Single Song");
		repairType[0].setSelection(true);

		repairType[1] = new Button(shell, SWT.RADIO);
		repairType[1].setText("Song Directory");
		repairType[1].setSelection(false);

		// File group
		Group fileGroup = new Group(shell, SWT.NONE);
		fileGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fileGroup.setLayout(new GridLayout(2, false));
		fileGroup.setText("Song File");

		Text fileInput = new Text(fileGroup, SWT.SINGLE | SWT.BORDER);
		fileInput.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fileInput.setText(Settings.getInstance().getInputFile());

		dialog = new FileDialog(shell, SWT.OPEN);
		((FileDialog) dialog).setFilterExtensions(new String[] { "info.dat" });
		((FileDialog) dialog).setFilterNames(new String[] { "Custom Song" });

		repairType[0].addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				dialog = new FileDialog(shell, SWT.OPEN);
				((FileDialog) dialog).setFilterExtensions(new String[] { "info.dat" });
				((FileDialog) dialog).setFilterNames(new String[] { "Custom Song" });
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent selectionEvent) {

			}
		});

		repairType[1].addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				dialog = new DirectoryDialog(shell, SWT.OPEN);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent selectionEvent) {

			}
		});

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
		fileInput.addModifyListener(e ->
				statusText.setText("File path manually altered, choose repair to attempt to repair the file."));

		browse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(dialog instanceof DirectoryDialog){
					// Restore last directory
					((DirectoryDialog) dialog).setFilterPath(Settings.getInstance().getBrowseDirectory());

					// Open dialog
					String file = ((DirectoryDialog)dialog).open();
					if (file != null) {
						fileInput.setText(file);
						statusText.setText("File selected from dialog, choose repair to attempt to repair the file.");
						Settings.getInstance().saveInputFile(file);
						Settings.getInstance().saveBrowseDirectory(((DirectoryDialog) dialog).getFilterPath());
					}
				} else if(dialog instanceof FileDialog){
					// Restore last directory
					((FileDialog) dialog).setFilterPath(Settings.getInstance().getBrowseDirectory());

					// Open dialog
					String file = ((FileDialog)dialog).open();
					if (file != null) {
						fileInput.setText(file);
						statusText.setText("File selected from dialog, choose repair to attempt to repair the file.");
						Settings.getInstance().saveInputFile(file);
						Settings.getInstance().saveBrowseDirectory(((FileDialog) dialog).getFilterPath());
					}
				}

			}
		});

		repair.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Settings.getInstance().saveInputFile(fileInput.getText());

				RepairType repairType = null;

				if(dialog instanceof DirectoryDialog)
					repairType = RepairType.SONG_DIRECTORY;
				else if (dialog instanceof FileDialog)
					repairType = RepairType.SINGLE_SONG;

				java.util.List<RepairResult> status = RepairFileEntry.repairFile(fileInput.getText(), repairType);

				StringBuilder resultBody = new StringBuilder();

				for(RepairResult result : status){
					resultBody.append(result.toString()).append("\n");
				}

				statusText.setText(resultBody.toString());

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
					statusText.setText("Unexpected problem occurred:\n" + sw);
				}
			}
		}
	}

	@Override
	public void close() {
		if (icon != null) {
			icon.dispose();
		}
		if (display != null) {
			display.dispose();
		}
	}
}
