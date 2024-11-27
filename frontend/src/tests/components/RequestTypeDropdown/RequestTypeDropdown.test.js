import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { requestTypeFixtures } from 'fixtures/requestFixtures';
import RequestTypeDropdown from '../../../main/components/RequestTypeDropdown/RequestTypeDropdown';

describe('RequestTypeDropdown Component', () => {
  const options = [
    { value: 'Scholarship or Fellowship', label: 'POST Request' },
    { value: 'MS program (other than CS Dept BS/MS)', label: 'PUT Request' },
    { value: 'PhD program', label: 'DELETE Request' },
  ];

  test('renders without crashing', () => {
    render(<RequestTypeDropdown options={options} requests={requestTypeFixtures.threeRequestTypes} onChange={() => {}} />);
    const dropdown = screen.getByRole('combobox');
    expect(dropdown).toBeInTheDocument();
  });

  test('renders all options correctly', () => {
    render(<RequestTypeDropdown options={options} requests={requestTypeFixtures.threeRequestTypes} onChange={() => {}} />);
    fireEvent.mouseDown(screen.getByRole('combobox'));
    options.forEach(option => {
      expect(screen.getByText(option.value)).toBeInTheDocument();
    });
  });

});
